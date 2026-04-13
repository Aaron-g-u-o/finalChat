package com.abin.mallchat.common.server.domain.vo.request;

import lombok.Data;

/**
 * 加入语音频道请求
 */
@Data
public class VoiceJoinRequest {
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 服务器ID
     */
    private Long serverId;
    /**
     * 频道ID
     */
    private Long channelId;
}
