package com.abin.mallchat.common.server.service;

import com.abin.mallchat.common.server.domain.vo.request.VoiceJoinRequest;
import com.abin.mallchat.common.server.domain.vo.request.VoiceLeaveRequest;
import com.abin.mallchat.common.server.domain.vo.response.VoiceChannelInfoResp;
import com.abin.mallchat.common.server.domain.vo.response.VoiceMemberResp;

import java.util.List;

/**
 * 语音服务接口
 */
public interface IVoiceService {
    /**
     * 加入语音频道
     */
    VoiceChannelInfoResp joinChannel(VoiceJoinRequest request);

    /**
     * 离开语音频道
     */
    void leaveChannel(VoiceLeaveRequest request);

    /**
     * 获取语音频道信息
     */
    VoiceChannelInfoResp getChannelInfo(Long channelId);

    /**
     * 获取语音频道成员列表
     */
    List<VoiceMemberResp> getChannelMembers(Long channelId);
}
