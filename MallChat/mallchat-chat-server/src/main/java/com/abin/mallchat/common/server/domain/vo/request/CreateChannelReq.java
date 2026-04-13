package com.abin.mallchat.common.server.domain.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateChannelReq {
    @ApiModelProperty(value = "服务器ID")
    @NotNull
    private Long serverId;

    @ApiModelProperty(value = "频道名称")
    @NotBlank
    private String name;

    @ApiModelProperty(value = "频道类型: TEXT(文字), VOICE(语音)")
    @NotBlank
    private String type;

    @ApiModelProperty(value = "频道位置")
    private Integer position;
}
