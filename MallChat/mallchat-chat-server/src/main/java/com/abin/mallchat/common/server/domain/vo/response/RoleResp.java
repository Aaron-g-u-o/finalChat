package com.abin.mallchat.common.server.domain.vo.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleResp {
    @ApiModelProperty(value = "角色ID")
    private Long id;

    @ApiModelProperty(value = "服务器ID")
    private Long serverId;

    @ApiModelProperty(value = "角色名称")
    private String name;

    @ApiModelProperty(value = "角色颜色")
    private String color;

    @ApiModelProperty(value = "权限列表")
    private List<String> permissions;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
