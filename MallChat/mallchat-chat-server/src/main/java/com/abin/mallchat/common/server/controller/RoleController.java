package com.abin.mallchat.common.server.controller;

import com.abin.mallchat.common.common.domain.vo.response.ApiResult;
import com.abin.mallchat.common.common.utils.RequestHolder;
import com.abin.mallchat.common.server.domain.vo.request.CreateRoleReq;
import com.abin.mallchat.common.server.domain.vo.response.RoleResp;
import com.abin.mallchat.common.server.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/capi/role")
@Api(tags = "角色相关接口")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/create")
    @ApiOperation("创建角色")
    public ApiResult<RoleResp> createRole(@Valid @RequestBody CreateRoleReq req) {
        return ApiResult.success(roleService.createRole(RequestHolder.get().getUid(), req));
    }

    @DeleteMapping("/{roleId}")
    @ApiOperation("删除角色")
    public ApiResult<Void> deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(RequestHolder.get().getUid(), roleId);
        return ApiResult.success();
    }

    @PutMapping("/{roleId}")
    @ApiOperation("更新角色")
    public ApiResult<RoleResp> updateRole(@PathVariable Long roleId, @Valid @RequestBody CreateRoleReq req) {
        return ApiResult.success(roleService.updateRole(RequestHolder.get().getUid(), roleId, req));
    }

    @GetMapping("/{roleId}")
    @ApiOperation("获取角色详情")
    public ApiResult<RoleResp> getRole(@PathVariable Long roleId) {
        return ApiResult.success(roleService.getRole(roleId));
    }

    @GetMapping("/server/{serverId}")
    @ApiOperation("获取服务器角色列表")
    public ApiResult<List<RoleResp>> getServerRoles(@PathVariable Long serverId) {
        return ApiResult.success(roleService.getServerRoles(serverId));
    }
}
