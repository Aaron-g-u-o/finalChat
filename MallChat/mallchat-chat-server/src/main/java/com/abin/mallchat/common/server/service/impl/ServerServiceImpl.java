package com.abin.mallchat.common.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.abin.mallchat.common.common.utils.AssertUtil;
import com.abin.mallchat.common.server.domain.entity.Server;
import com.abin.mallchat.common.server.domain.entity.ServerMember;
import com.abin.mallchat.common.server.domain.vo.request.CreateServerReq;
import com.abin.mallchat.common.server.domain.vo.response.ServerResp;
import com.abin.mallchat.common.server.mapper.ServerMapper;
import com.abin.mallchat.common.server.mapper.ServerMemberMapper;
import com.abin.mallchat.common.server.service.ServerService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServerServiceImpl implements ServerService {

    @Autowired
    private ServerMapper serverMapper;

    @Autowired
    private ServerMemberMapper serverMemberMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResp createServer(Long uid, CreateServerReq req) {
        Server server = Server.builder()
                .name(req.getName())
                .icon(req.getIcon())
                .description(req.getDescription())
                .creatorId(uid)
                .createTime(new Date())
                .updateTime(new Date())
                .build();
        serverMapper.insert(server);

        ServerMember member = ServerMember.builder()
                .serverId(server.getId())
                .userId(uid)
                .joinTime(new Date())
                .build();
        serverMemberMapper.insert(member);

        return BeanUtil.copyProperties(server, ServerResp.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteServer(Long uid, Long serverId) {
        Server server = serverMapper.selectById(serverId);
        AssertUtil.isNotEmpty(server, "服务器不存在");
        AssertUtil.equal(server.getCreatorId(), uid, "只有创建者才能删除服务器");

        serverMapper.deleteById(serverId);

        LambdaQueryWrapper<ServerMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServerMember::getServerId, serverId);
        serverMemberMapper.delete(wrapper);
    }

    @Override
    public ServerResp updateServer(Long uid, Long serverId, CreateServerReq req) {
        Server server = serverMapper.selectById(serverId);
        AssertUtil.isNotEmpty(server, "服务器不存在");
        AssertUtil.equal(server.getCreatorId(), uid, "只有创建者才能修改服务器");

        server.setName(req.getName());
        server.setIcon(req.getIcon());
        server.setDescription(req.getDescription());
        server.setUpdateTime(new Date());
        serverMapper.updateById(server);

        return BeanUtil.copyProperties(server, ServerResp.class);
    }

    @Override
    public ServerResp getServer(Long serverId) {
        Server server = serverMapper.selectById(serverId);
        AssertUtil.isNotEmpty(server, "服务器不存在");
        return BeanUtil.copyProperties(server, ServerResp.class);
    }

    @Override
    public List<ServerResp> getJoinedServers(Long uid) {
        LambdaQueryWrapper<ServerMember> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(ServerMember::getUserId, uid);
        List<ServerMember> members = serverMemberMapper.selectList(memberWrapper);

        List<Long> serverIds = members.stream()
                .map(ServerMember::getServerId)
                .collect(Collectors.toList());

        if (serverIds.isEmpty()) {
            return List.of();
        }

        LambdaQueryWrapper<Server> serverWrapper = new LambdaQueryWrapper<>();
        serverWrapper.in(Server::getId, serverIds);
        List<Server> servers = serverMapper.selectList(serverWrapper);

        return servers.stream()
                .map(server -> BeanUtil.copyProperties(server, ServerResp.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void joinServer(Long uid, Long serverId) {
        Server server = serverMapper.selectById(serverId);
        AssertUtil.isNotEmpty(server, "服务器不存在");

        LambdaQueryWrapper<ServerMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServerMember::getServerId, serverId)
                .eq(ServerMember::getUserId, uid);
        ServerMember existingMember = serverMemberMapper.selectOne(wrapper);
        AssertUtil.isEmpty(existingMember, "已经加入过该服务器");

        ServerMember member = ServerMember.builder()
                .serverId(serverId)
                .userId(uid)
                .joinTime(new Date())
                .build();
        serverMemberMapper.insert(member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void leaveServer(Long uid, Long serverId) {
        Server server = serverMapper.selectById(serverId);
        AssertUtil.isNotEmpty(server, "服务器不存在");

        if (server.getCreatorId().equals(uid)) {
            deleteServer(uid, serverId);
            return;
        }

        LambdaQueryWrapper<ServerMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServerMember::getServerId, serverId)
                .eq(ServerMember::getUserId, uid);
        serverMemberMapper.delete(wrapper);
    }
}
