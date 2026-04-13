package com.abin.mallchat.common.server.controller;

import com.abin.mallchat.common.common.domain.vo.response.ApiResult;
import com.abin.mallchat.common.common.utils.RequestHolder;
import com.abin.mallchat.common.server.domain.vo.request.CreateServerReq;
import com.abin.mallchat.common.server.domain.vo.response.ServerResp;
import com.abin.mallchat.common.server.service.ServerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/capi/server")
@Api(tags = "服务器相关接口")
public class ServerController {

    @Autowired
    private ServerService serverService;

    @PostMapping("/create")
    @ApiOperation("创建服务器")
    public ApiResult<ServerResp> createServer(@Valid @RequestBody CreateServerReq req) {
        return ApiResult.success(serverService.createServer(RequestHolder.get().getUid(), req));
    }

    @DeleteMapping("/{serverId}")
    @ApiOperation("删除服务器")
    public ApiResult<Void> deleteServer(@PathVariable Long serverId) {
        serverService.deleteServer(RequestHolder.get().getUid(), serverId);
        return ApiResult.success();
    }

    @PutMapping("/{serverId}")
    @ApiOperation("更新服务器")
    public ApiResult<ServerResp> updateServer(@PathVariable Long serverId, @Valid @RequestBody CreateServerReq req) {
        return ApiResult.success(serverService.updateServer(RequestHolder.get().getUid(), serverId, req));
    }

    @GetMapping("/{serverId}")
    @ApiOperation("获取服务器详情")
    public ApiResult<ServerResp> getServer(@PathVariable Long serverId) {
        return ApiResult.success(serverService.getServer(serverId));
    }

    @GetMapping("/my")
    @ApiOperation("获取我加入的服务器列表")
    public ApiResult<List<ServerResp>> getJoinedServers() {
        return ApiResult.success(serverService.getJoinedServers(RequestHolder.get().getUid()));
    }

    @PostMapping("/{serverId}/join")
    @ApiOperation("加入服务器")
    public ApiResult<Void> joinServer(@PathVariable Long serverId) {
        serverService.joinServer(RequestHolder.get().getUid(), serverId);
        return ApiResult.success();
    }

    @PostMapping("/{serverId}/leave")
    @ApiOperation("离开服务器")
    public ApiResult<Void> leaveServer(@PathVariable Long serverId) {
        serverService.leaveServer(RequestHolder.get().getUid(), serverId);
        return ApiResult.success();
    }
}
