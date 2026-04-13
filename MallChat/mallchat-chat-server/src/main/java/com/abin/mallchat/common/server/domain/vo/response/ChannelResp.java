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
public class ChannelResp {
    @ApiModelProperty(value = "频道ID")
    private Long id;

    @ApiModelProperty(value = "服务器ID")
    private Long serverId;

    @ApiModelProperty(value = "频道名称")
    private String name;

    @ApiModelProperty(value = "频道类型")
    private String type;

    @ApiModelProperty(value = "频道位置")
    private Integer position;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
