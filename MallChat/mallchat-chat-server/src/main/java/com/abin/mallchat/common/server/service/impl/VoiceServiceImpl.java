package com.abin.mallchat.common.server.service.impl;

import com.abin.mallchat.common.server.domain.vo.request.VoiceJoinRequest;
import com.abin.mallchat.common.server.domain.vo.request.VoiceLeaveRequest;
import com.abin.mallchat.common.server.domain.vo.response.VoiceChannelInfoResp;
import com.abin.mallchat.common.server.domain.vo.response.VoiceMemberResp;
import com.abin.mallchat.common.server.service.IVoiceService;
import com.abin.mallchat.common.user.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 语音服务实现
 */
@Service
@Slf4j
public class VoiceServiceImpl implements IVoiceService {

    @Autowired
    private WebSocketService webSocketService;

    @Override
    public VoiceChannelInfoResp joinChannel(VoiceJoinRequest request) {
        // 这里可以添加业务逻辑，比如检查用户是否有权限加入频道
        // 目前暂时直接返回频道信息
        return VoiceChannelInfoResp.builder()
                .channelId(request.getChannelId())
                .channelName("语音频道")
                .serverId(request.getServerId())
                .build();
    }

    @Override
    public void leaveChannel(VoiceLeaveRequest request) {
        // 这里可以添加业务逻辑，比如记录用户离开时间等
        log.info("用户{}离开语音频道{}", request.getUserId(), request.getChannelId());
    }

    @Override
    public VoiceChannelInfoResp getChannelInfo(Long channelId) {
        // 这里应该从数据库获取频道信息，暂时返回模拟数据
        return VoiceChannelInfoResp.builder()
                .channelId(channelId)
                .channelName("语音频道")
                .serverId(1L)
                .build();
    }

    @Override
    public List<VoiceMemberResp> getChannelMembers(Long channelId) {
        // 这里应该从WebSocket服务中获取在线用户列表，暂时返回模拟数据
        List<VoiceMemberResp> members = new ArrayList<>();
        members.add(VoiceMemberResp.builder()
                .userId(1L)
                .userName("用户1")
                .avatar("https://example.com/avatar1.jpg")
                .build());
        members.add(VoiceMemberResp.builder()
                .userId(2L)
                .userName("用户2")
                .avatar("https://example.com/avatar2.jpg")
                .build());
        return members;
    }
}
