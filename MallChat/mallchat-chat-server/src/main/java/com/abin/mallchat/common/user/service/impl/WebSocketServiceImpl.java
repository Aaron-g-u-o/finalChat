package com.abin.mallchat.common.user.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.abin.mallchat.common.common.config.ThreadPoolConfig;
import com.abin.mallchat.common.common.constant.RedisKey;
import com.abin.mallchat.common.common.event.UserOfflineEvent;
import com.abin.mallchat.common.common.event.UserOnlineEvent;
import com.abin.mallchat.common.common.utils.RedisUtils;
import com.abin.mallchat.common.user.dao.UserDao;
import com.abin.mallchat.common.user.domain.dto.WSChannelExtraDTO;
import com.abin.mallchat.common.user.domain.entity.User;
import com.abin.mallchat.common.user.domain.enums.RoleEnum;
import com.abin.mallchat.common.user.domain.enums.WSBaseResp;
import com.abin.mallchat.common.user.domain.vo.request.ws.WSAuthorize;
import com.abin.mallchat.common.user.domain.vo.request.ws.WSVoiceJoinReq;
import com.abin.mallchat.common.user.domain.vo.request.ws.WSVoiceSignalingReq;
import com.abin.mallchat.common.user.service.IRoleService;
import com.abin.mallchat.common.user.service.LoginService;
import com.abin.mallchat.common.user.service.WebSocketService;
import com.abin.mallchat.common.user.service.adapter.WSAdapter;
import com.abin.mallchat.common.user.service.cache.UserCache;
import com.abin.mallchat.common.websocket.NettyUtil;
import com.abin.mallchat.transaction.service.MQProducer;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Description: websocket处理类
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19 16:21
 */
@Component
@Slf4j
public class WebSocketServiceImpl implements WebSocketService {

    private static final Duration EXPIRE_TIME = Duration.ofHours(1);
    private static final Long MAX_MUM_SIZE = 10000L;
    /**
     * 所有请求登录的code与channel关系
     */
    public static final Cache<Integer, Channel> WAIT_LOGIN_MAP = Caffeine.newBuilder()
            .expireAfterWrite(EXPIRE_TIME)
            .maximumSize(MAX_MUM_SIZE)
            .build();
    /**
     * 所有已连接的websocket连接列表和一些额外参数
     */
    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> ONLINE_WS_MAP = new ConcurrentHashMap<>();
    /**
     * 所有在线的用户和对应的socket
     */
    private static final ConcurrentHashMap<Long, CopyOnWriteArrayList<Channel>> ONLINE_UID_MAP = new ConcurrentHashMap<>();
    
    /**
     * 语音频道用户映射，key: 语音频道ID, value: 用户ID列表
     */
    private static final ConcurrentHashMap<Long, CopyOnWriteArrayList<Long>> VOICE_CHANNEL_USERS_MAP = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<Channel, WSChannelExtraDTO> getOnlineMap() {
        return ONLINE_WS_MAP;
    }

    /**
     * redis保存loginCode的key
     */
    private static final String LOGIN_CODE = "loginCode";
    @Autowired
    private WxMpService wxMpService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    @Qualifier(ThreadPoolConfig.WS_EXECUTOR)
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Autowired
    private UserCache userCache;
    @Autowired
    private IRoleService iRoleService;
    @Autowired
    private MQProducer mqProducer;

    /**
     * 处理用户登录请求，需要返回一张带code的二维码
     *
     * @param channel
     */
    @SneakyThrows
    @Override
    public void handleLoginReq(Channel channel) {
        //生成随机不重复的登录码,并将channel存在本地cache中
        Integer code = generateLoginCode(channel);
        //请求微信接口，获取登录码地址
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(code, (int) EXPIRE_TIME.getSeconds());
        //返回给前端（channel必在本地）
        sendMsg(channel, WSAdapter.buildLoginResp(wxMpQrCodeTicket));
    }

    /**
     * 获取不重复的登录的code，微信要求最大不超过int的存储极限
     * 防止并发，可以给方法加上synchronize，也可以使用cas乐观锁
     *
     * @return
     */
    private Integer generateLoginCode(Channel channel) {
        int inc;
        do {
            //本地cache时间必须比redis key过期时间短，否则会出现并发问题
            inc = RedisUtils.integerInc(RedisKey.getKey(LOGIN_CODE), (int) EXPIRE_TIME.toMinutes(), TimeUnit.MINUTES);
        } while (WAIT_LOGIN_MAP.asMap().containsKey(inc));
        //储存一份在本地
        WAIT_LOGIN_MAP.put(inc, channel);
        return inc;
    }

    /**
     * 处理所有ws连接的事件
     *
     * @param channel
     */
    @Override
    public void connect(Channel channel) {
        ONLINE_WS_MAP.put(channel, new WSChannelExtraDTO());
    }

    @Override
    public void removed(Channel channel) {
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.get(channel);
        Optional<Long> uidOptional = Optional.ofNullable(wsChannelExtraDTO)
                .map(WSChannelExtraDTO::getUid);
        boolean offlineAll = offline(channel, uidOptional);
        if (uidOptional.isPresent() && offlineAll) {//已登录用户断连,并且全下线成功
            User user = new User();
            user.setId(uidOptional.get());
            user.setLastOptTime(new Date());
            applicationEventPublisher.publishEvent(new UserOfflineEvent(this, user));
        }
    }

    @Override
    public void authorize(Channel channel, WSAuthorize wsAuthorize) {
        //校验token
        boolean verifySuccess = loginService.verify(wsAuthorize.getToken());
        if (verifySuccess) {//用户校验成功给用户登录
            User user = userDao.getById(loginService.getValidUid(wsAuthorize.getToken()));
            loginSuccess(channel, user, wsAuthorize.getToken());
        } else { //让前端的token失效
            sendMsg(channel, WSAdapter.buildInvalidateTokenResp());
        }
    }

    /**
     * (channel必在本地)登录成功，并更新状态
     */
    private void loginSuccess(Channel channel, User user, String token) {
        //更新上线列表
        online(channel, user.getId());
        //返回给用户登录成功
        boolean hasPower = iRoleService.hasPower(user.getId(), RoleEnum.CHAT_MANAGER);
        //发送给对应的用户
        sendMsg(channel, WSAdapter.buildLoginSuccessResp(user, token, hasPower));
        //发送用户上线事件
        boolean online = userCache.isOnline(user.getId());
        if (!online) {
            user.setLastOptTime(new Date());
            user.refreshIp(NettyUtil.getAttr(channel, NettyUtil.IP));
            applicationEventPublisher.publishEvent(new UserOnlineEvent(this, user));
        }
    }

    /**
     * 用户上线
     */
    private void online(Channel channel, Long uid) {
        getOrInitChannelExt(channel).setUid(uid);
        ONLINE_UID_MAP.putIfAbsent(uid, new CopyOnWriteArrayList<>());
        ONLINE_UID_MAP.get(uid).add(channel);
        NettyUtil.setAttr(channel, NettyUtil.UID, uid);
    }

    /**
     * 用户下线
     * return 是否全下线成功
     */
    private boolean offline(Channel channel, Optional<Long> uidOptional) {
        ONLINE_WS_MAP.remove(channel);
        if (uidOptional.isPresent()) {
            CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uidOptional.get());
            if (CollectionUtil.isNotEmpty(channels)) {
                channels.removeIf(ch -> Objects.equals(ch, channel));
            }
            return CollectionUtil.isEmpty(ONLINE_UID_MAP.get(uidOptional.get()));
        }
        return true;
    }

    @Override
    public Boolean scanLoginSuccess(Integer loginCode, Long uid) {
        //确认连接在该机器
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(loginCode);
        if (Objects.isNull(channel)) {
            return Boolean.FALSE;
        }
        User user = userDao.getById(uid);
        //移除code
        WAIT_LOGIN_MAP.invalidate(loginCode);
        //调用用户登录模块
        String token = loginService.login(uid);
        //用户登录
        loginSuccess(channel, user, token);
        return Boolean.TRUE;
    }

    @Override
    public Boolean scanSuccess(Integer loginCode) {
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(loginCode);
        if (Objects.nonNull(channel)) {
            sendMsg(channel, WSAdapter.buildScanSuccessResp());
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }


    /**
     * 如果在线列表不存在，就先把该channel放进在线列表
     *
     * @param channel
     * @return
     */
    private WSChannelExtraDTO getOrInitChannelExt(Channel channel) {
        WSChannelExtraDTO wsChannelExtraDTO =
                ONLINE_WS_MAP.getOrDefault(channel, new WSChannelExtraDTO());
        WSChannelExtraDTO old = ONLINE_WS_MAP.putIfAbsent(channel, wsChannelExtraDTO);
        return ObjectUtil.isNull(old) ? wsChannelExtraDTO : old;
    }

    //entrySet的值不是快照数据,但是它支持遍历，所以无所谓了，不用快照也行。
    @Override
    public void sendToAllOnline(WSBaseResp<?> wsBaseResp, Long skipUid) {
        ONLINE_WS_MAP.forEach((channel, ext) -> {
            if (Objects.nonNull(skipUid) && Objects.equals(ext.getUid(), skipUid)) {
                return;
            }
            threadPoolTaskExecutor.execute(() -> sendMsg(channel, wsBaseResp));
        });
    }

    @Override
    public void sendToAllOnline(WSBaseResp<?> wsBaseResp) {
        sendToAllOnline(wsBaseResp, null);
    }

    @Override
    public void sendToUid(WSBaseResp<?> wsBaseResp, Long uid) {
        CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uid);
        if (CollectionUtil.isEmpty(channels)) {
            log.info("用户：{}不在线", uid);
            return;
        }
        channels.forEach(channel -> {
            threadPoolTaskExecutor.execute(() -> sendMsg(channel, wsBaseResp));
        });
    }


    /**
     * 给本地channel发送消息
     *
     * @param channel
     * @param wsBaseResp
     */
    private void sendMsg(Channel channel, WSBaseResp<?> wsBaseResp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(wsBaseResp)));
    }

    @Override
    public void handleVoiceJoin(Channel channel, WSVoiceJoinReq req) {
        WSChannelExtraDTO channelExtra = getOrInitChannelExt(channel);
        Long uid = channelExtra.getUid();
        if (uid == null) {
            return;
        }

        // 先离开之前的语音频道
        if (channelExtra.getVoiceChannelId() != null) {
            handleVoiceLeave(channel, null);
        }

        // 更新频道信息
        channelExtra.setVoiceChannelId(req.getChannelId());
        channelExtra.setServerId(req.getServerId());

        // 添加到语音频道用户列表
        VOICE_CHANNEL_USERS_MAP.putIfAbsent(req.getChannelId(), new CopyOnWriteArrayList<>());
        CopyOnWriteArrayList<Long> users = VOICE_CHANNEL_USERS_MAP.get(req.getChannelId());
        if (!users.contains(uid)) {
            users.add(uid);
        }

        // 通知频道内其他用户有新用户加入
        notifyVoiceChannelUsers(req.getChannelId(), uid, "VOICE_JOIN");
    }

    @Override
    public void handleVoiceLeave(Channel channel, Long channelId) {
        WSChannelExtraDTO channelExtra = getOrInitChannelExt(channel);
        Long uid = channelExtra.getUid();
        if (uid == null) {
            return;
        }

        Long voiceChannelId = channelId != null ? channelId : channelExtra.getVoiceChannelId();
        if (voiceChannelId == null) {
            return;
        }

        // 从语音频道用户列表中移除
        CopyOnWriteArrayList<Long> users = VOICE_CHANNEL_USERS_MAP.get(voiceChannelId);
        if (users != null) {
            users.remove(uid);
            if (users.isEmpty()) {
                VOICE_CHANNEL_USERS_MAP.remove(voiceChannelId);
            }
        }

        // 清空频道信息
        channelExtra.setVoiceChannelId(null);
        channelExtra.setServerId(null);

        // 通知频道内其他用户有用户离开
        notifyVoiceChannelUsers(voiceChannelId, uid, "VOICE_LEAVE");
    }

    @Override
    public void handleVoiceSignaling(Channel channel, WSVoiceSignalingReq req) {
        WSChannelExtraDTO channelExtra = getOrInitChannelExt(channel);
        Long uid = channelExtra.getUid();
        Long voiceChannelId = channelExtra.getVoiceChannelId();

        if (uid == null || voiceChannelId == null) {
            return;
        }

        // 转发信令给指定用户或频道内所有用户
        if (req.getTargetUid() != null) {
            // 转发给指定用户
            sendToUid(WSAdapter.buildVoiceSignalingResp(req.getType(), req.getPayload(), uid), req.getTargetUid());
        } else {
            // 广播给频道内所有用户
            CopyOnWriteArrayList<Long> users = VOICE_CHANNEL_USERS_MAP.get(voiceChannelId);
            if (users != null) {
                users.forEach(targetUid -> {
                    if (!targetUid.equals(uid)) {
                        sendToUid(WSAdapter.buildVoiceSignalingResp(req.getType(), req.getPayload(), uid), targetUid);
                    }
                });
            }
        }
    }

    /**
     * 通知语音频道内的用户
     */
    private void notifyVoiceChannelUsers(Long channelId, Long uid, String type) {
        CopyOnWriteArrayList<Long> users = VOICE_CHANNEL_USERS_MAP.get(channelId);
        if (users != null) {
            users.forEach(targetUid -> {
                if (!targetUid.equals(uid)) {
                    sendToUid(WSAdapter.buildVoiceStatusResp(type, uid, channelId), targetUid);
                }
            });
        }
    }

}
