package com.abin.mallchat.common.server.controller;

import com.abin.mallchat.common.common.annotation.RequestRequiredParam;
import com.abin.mallchat.common.common.domain.vo.RespVO;
import com.abin.mallchat.common.server.domain.vo.request.VoiceJoinRequest;
import com.abin.mallchat.common.server.domain.vo.request.VoiceLeaveRequest;
import com.abin.mallchat.common.server.domain.vo.response.VoiceChannelInfoResp;
import com.abin.mallchat.common.server.domain.vo.response.VoiceMemberResp;
import com.abin.mallchat.common.server.service.IVoiceService;
import com.abin.mallchat.common.user.domain.enums.RoleEnum;
import com.abin.mallchat.common.user.service.IRoleService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 语音控制器
 */
@RestController
@RequestMapping("/api/voice")
public class VoiceController {

    @Autowired
    private IVoiceService voiceService;

    @Autowired
    private IRoleService roleService;

    @ApiOperation("加入语音频道")
    @PostMapping("/join")
    public RespVO<VoiceChannelInfoResp> joinChannel(@RequestBody VoiceJoinRequest request, HttpServletRequest httpServletRequest) {
        Long uid = (Long) httpServletRequest.getAttribute("uid");
        request.setUserId(uid);
        return RespVO.success(voiceService.joinChannel(request));
    }

    @ApiOperation("离开语音频道")
    @PostMapping("/leave")
    public RespVO<Void> leaveChannel(@RequestBody VoiceLeaveRequest request, HttpServletRequest httpServletRequest) {
        Long uid = (Long) httpServletRequest.getAttribute("uid");
        request.setUserId(uid);
        voiceService.leaveChannel(request);
        return RespVO.success();
    }

    @ApiOperation("获取语音频道信息")
    @GetMapping("/channel/info")
    public RespVO<VoiceChannelInfoResp> getChannelInfo(@RequestRequiredParam Long channelId) {
        return RespVO.success(voiceService.getChannelInfo(channelId));
    }

    @ApiOperation("获取语音频道成员列表")
    @GetMapping("/channel/members")
    public RespVO<List<VoiceMemberResp>> getChannelMembers(@RequestRequiredParam Long channelId) {
        return RespVO.success(voiceService.getChannelMembers(channelId));
    }
}
