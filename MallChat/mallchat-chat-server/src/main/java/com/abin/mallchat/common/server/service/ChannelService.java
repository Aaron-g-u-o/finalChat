package com.abin.mallchat.common.server.service;

import com.abin.mallchat.common.server.domain.vo.request.CreateChannelReq;
import com.abin.mallchat.common.server.domain.vo.response.ChannelResp;

import java.util.List;

public interface ChannelService {

    ChannelResp createChannel(Long uid, CreateChannelReq req);

    void deleteChannel(Long uid, Long channelId);

    ChannelResp updateChannel(Long uid, Long channelId, CreateChannelReq req);

    ChannelResp getChannel(Long channelId);

    List<ChannelResp> getServerChannels(Long serverId);
}
