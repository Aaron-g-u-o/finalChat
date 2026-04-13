package com.abin.mallchat.common.server.controller;

import com.abin.mallchat.common.common.domain.vo.response.ApiResult;
import com.abin.mallchat.common.server.domain.vo.response.PermissionResp;
import com.abin.mallchat.common.server.service.PermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/capi/permission")
@Api(tags = "权限相关接口")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping("/all")
    @ApiOperation("获取所有权限")
    public ApiResult<List<PermissionResp>> getAllPermissions() {
        return ApiResult.success(permissionService.getAllPermissions());
    }

    @GetMapping("/code/{code}")
    @ApiOperation("根据代码获取权限")
    public ApiResult<PermissionResp> getPermissionByCode(@PathVariable String code) {
        return ApiResult.success(permissionService.getPermissionByCode(code));
    }
}
