package com.abin.mallchat.common.user.domain.vo.response.ws;

import lombok.Builder;
import lombok.Data;

/**
 * 语音状态响应
 */
@Data
@Builder
public class WSVoiceStatus {
    /**
     * 类型：VOICE_JOIN, VOICE_LEAVE
     */
    private String type;
    /**
     * 用户ID
     */
    private Long uid;
    /**
     * 语音频道ID
     */
    private Long channelId;
}
