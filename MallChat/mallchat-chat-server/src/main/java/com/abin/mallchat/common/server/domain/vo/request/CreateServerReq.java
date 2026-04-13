package com.abin.mallchat.common.server.domain.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateServerReq {
    @ApiModelProperty(value = "服务器名称")
    @NotBlank
    private String name;

    @ApiModelProperty(value = "服务器图标")
    private String icon;

    @ApiModelProperty(value = "服务器描述")
    private String description;
}
