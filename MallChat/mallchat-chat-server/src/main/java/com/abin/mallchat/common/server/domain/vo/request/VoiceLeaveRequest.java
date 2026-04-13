package com.abin.mallchat.common.server.domain.vo.request;

import lombok.Data;

/**
 * 离开语音频道请求
 */
@Data
public class VoiceLeaveRequest {
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 频道ID
     */
    private Long channelId;
}
