package com.abin.mallchat.common.server.domain.vo.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServerResp {
    @ApiModelProperty(value = "服务器ID")
    private Long id;

    @ApiModelProperty(value = "服务器名称")
    private String name;

    @ApiModelProperty(value = "服务器图标")
    private String icon;

    @ApiModelProperty(value = "服务器描述")
    private String description;

    @ApiModelProperty(value = "创建者ID")
    private Long creatorId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
