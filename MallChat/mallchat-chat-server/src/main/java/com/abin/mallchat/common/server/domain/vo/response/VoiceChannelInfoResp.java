package com.abin.mallchat.common.server.domain.vo.response;

import lombok.Builder;
import lombok.Data;

/**
 * 语音频道信息响应
 */
@Data
@Builder
public class VoiceChannelInfoResp {
    /**
     * 频道ID
     */
    private Long channelId;
    /**
     * 频道名称
     */
    private String channelName;
    /**
     * 服务器ID
     */
    private Long serverId;
    /**
     * 在线人数
     */
    private Integer onlineCount;
}
