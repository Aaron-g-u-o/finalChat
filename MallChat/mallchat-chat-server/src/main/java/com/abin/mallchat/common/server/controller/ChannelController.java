package com.abin.mallchat.common.server.controller;

import com.abin.mallchat.common.common.domain.vo.response.ApiResult;
import com.abin.mallchat.common.common.utils.RequestHolder;
import com.abin.mallchat.common.server.domain.vo.request.CreateChannelReq;
import com.abin.mallchat.common.server.domain.vo.response.ChannelResp;
import com.abin.mallchat.common.server.service.ChannelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/capi/channel")
@Api(tags = "频道相关接口")
public class ChannelController {

    @Autowired
    private ChannelService channelService;

    @PostMapping("/create")
    @ApiOperation("创建频道")
    public ApiResult<ChannelResp> createChannel(@Valid @RequestBody CreateChannelReq req) {
        return ApiResult.success(channelService.createChannel(RequestHolder.get().getUid(), req));
    }

    @DeleteMapping("/{channelId}")
    @ApiOperation("删除频道")
    public ApiResult<Void> deleteChannel(@PathVariable Long channelId) {
        channelService.deleteChannel(RequestHolder.get().getUid(), channelId);
        return ApiResult.success();
    }

    @PutMapping("/{channelId}")
    @ApiOperation("更新频道")
    public ApiResult<ChannelResp> updateChannel(@PathVariable Long channelId, @Valid @RequestBody CreateChannelReq req) {
        return ApiResult.success(channelService.updateChannel(RequestHolder.get().getUid(), channelId, req));
    }

    @GetMapping("/{channelId}")
    @ApiOperation("获取频道详情")
    public ApiResult<ChannelResp> getChannel(@PathVariable Long channelId) {
        return ApiResult.success(channelService.getChannel(channelId));
    }

    @GetMapping("/server/{serverId}")
    @ApiOperation("获取服务器频道列表")
    public ApiResult<List<ChannelResp>> getServerChannels(@PathVariable Long serverId) {
        return ApiResult.success(channelService.getServerChannels(serverId));
    }
}
