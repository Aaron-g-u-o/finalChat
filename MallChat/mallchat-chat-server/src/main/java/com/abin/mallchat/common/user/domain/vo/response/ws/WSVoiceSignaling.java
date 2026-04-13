package com.abin.mallchat.common.user.domain.vo.response.ws;

import lombok.Builder;
import lombok.Data;

/**
 * 语音信令响应
 */
@Data
@Builder
public class WSVoiceSignaling {
    /**
     * 类型：OFFER, ANSWER, CANDIDATE
     */
    private String type;
    /**
     * 信令数据
     */
    private String payload;
    /**
     * 发送者ID
     */
    private Long fromUid;
}
