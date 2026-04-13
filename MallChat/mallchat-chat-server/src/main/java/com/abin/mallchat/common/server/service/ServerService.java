package com.abin.mallchat.common.server.service;

import com.abin.mallchat.common.server.domain.vo.request.CreateServerReq;
import com.abin.mallchat.common.server.domain.vo.response.ServerResp;

import java.util.List;

public interface ServerService {

    ServerResp createServer(Long uid, CreateServerReq req);

    void deleteServer(Long uid, Long serverId);

    ServerResp updateServer(Long uid, Long serverId, CreateServerReq req);

    ServerResp getServer(Long serverId);

    List<ServerResp> getJoinedServers(Long uid);

    void joinServer(Long uid, Long serverId);

    void leaveServer(Long uid, Long serverId);
}
