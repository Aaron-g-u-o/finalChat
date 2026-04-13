package com.abin.mallchat.common.server.domain.vo.request.ws;

import lombok.Data;

@Data
public class WSVoiceJoinReq {
    private Long channelId;
    private Long userId;
}
