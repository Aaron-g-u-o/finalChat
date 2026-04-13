package com.abin.mallchat.common.server.domain.vo.response;

import lombok.Builder;
import lombok.Data;

/**
 * 语音频道成员响应
 */
@Data
@Builder
public class VoiceMemberResp {
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 是否正在说话
     */
    private Boolean isSpeaking;
}
