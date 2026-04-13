package com.abin.mallchat.common.server.domain.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRoleReq {
    @ApiModelProperty(value = "服务器ID")
    @NotNull
    private Long serverId;

    @ApiModelProperty(value = "角色名称")
    @NotBlank
    private String name;

    @ApiModelProperty(value = "角色颜色")
    private String color;

    @ApiModelProperty(value = "权限列表")
    private List<String> permissions;
}
