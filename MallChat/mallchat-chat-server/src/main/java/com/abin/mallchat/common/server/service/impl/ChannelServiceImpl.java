package com.abin.mallchat.common.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.abin.mallchat.common.common.utils.AssertUtil;
import com.abin.mallchat.common.server.domain.entity.Channel;
import com.abin.mallchat.common.server.domain.entity.Server;
import com.abin.mallchat.common.server.domain.entity.ServerMember;
import com.abin.mallchat.common.server.domain.vo.request.CreateChannelReq;
import com.abin.mallchat.common.server.domain.vo.response.ChannelResp;
import com.abin.mallchat.common.server.mapper.ChannelMapper;
import com.abin.mallchat.common.server.mapper.ServerMapper;
import com.abin.mallchat.common.server.mapper.ServerMemberMapper;
import com.abin.mallchat.common.server.service.ChannelService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChannelServiceImpl implements ChannelService {

    @Autowired
    private ChannelMapper channelMapper;

    @Autowired
    private ServerMapper serverMapper;

    @Autowired
    private ServerMemberMapper serverMemberMapper;

    @Override
    public ChannelResp createChannel(Long uid, CreateChannelReq req) {
        Server server = serverMapper.selectById(req.getServerId());
        AssertUtil.isNotEmpty(server, "服务器不存在");

        LambdaQueryWrapper<ServerMember> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(ServerMember::getServerId, req.getServerId())
                .eq(ServerMember::getUserId, uid);
        ServerMember member = serverMemberMapper.selectOne(memberWrapper);
        AssertUtil.isNotEmpty(member, "只有服务器成员才能创建频道");

        Channel channel = Channel.builder()
                .serverId(req.getServerId())
                .name(req.getName())
                .type(req.getType())
                .position(req.getPosition() != null ? req.getPosition() : 0)
                .createTime(new Date())
                .build();
        channelMapper.insert(channel);

        return BeanUtil.copyProperties(channel, ChannelResp.class);
    }

    @Override
    public void deleteChannel(Long uid, Long channelId) {
        Channel channel = channelMapper.selectById(channelId);
        AssertUtil.isNotEmpty(channel, "频道不存在");

        Server server = serverMapper.selectById(channel.getServerId());
        AssertUtil.isNotEmpty(server, "服务器不存在");
        AssertUtil.equal(server.getCreatorId(), uid, "只有创建者才能删除频道");

        channelMapper.deleteById(channelId);
    }

    @Override
    public ChannelResp updateChannel(Long uid, Long channelId, CreateChannelReq req) {
        Channel channel = channelMapper.selectById(channelId);
        AssertUtil.isNotEmpty(channel, "频道不存在");

        Server server = serverMapper.selectById(channel.getServerId());
        AssertUtil.isNotEmpty(server, "服务器不存在");
        AssertUtil.equal(server.getCreatorId(), uid, "只有创建者才能修改频道");

        channel.setName(req.getName());
        channel.setType(req.getType());
        channel.setPosition(req.getPosition());
        channelMapper.updateById(channel);

        return BeanUtil.copyProperties(channel, ChannelResp.class);
    }

    @Override
    public ChannelResp getChannel(Long channelId) {
        Channel channel = channelMapper.selectById(channelId);
        AssertUtil.isNotEmpty(channel, "频道不存在");
        return BeanUtil.copyProperties(channel, ChannelResp.class);
    }

    @Override
    public List<ChannelResp> getServerChannels(Long serverId) {
        LambdaQueryWrapper<Channel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Channel::getServerId, serverId)
                .orderByAsc(Channel::getPosition);
        List<Channel> channels = channelMapper.selectList(wrapper);

        return channels.stream()
                .map(channel -> BeanUtil.copyProperties(channel, ChannelResp.class))
                .collect(Collectors.toList());
    }
}
