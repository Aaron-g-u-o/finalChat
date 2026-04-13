package com.abin.mallchat.common.server.domain.vo.request.ws;

import lombok.Data;

@Data
public class WSVoiceSignalingReq {
    private String type;
    private Long targetUid;
    private String payload;
}
