package com.abin.mallchat.common.server.service;

import com.abin.mallchat.common.server.domain.vo.request.CreateRoleReq;
import com.abin.mallchat.common.server.domain.vo.response.RoleResp;

import java.util.List;

public interface RoleService {

    RoleResp createRole(Long uid, CreateRoleReq req);

    void deleteRole(Long uid, Long roleId);

    RoleResp updateRole(Long uid, Long roleId, CreateRoleReq req);

    RoleResp getRole(Long roleId);

    List<RoleResp> getServerRoles(Long serverId);
}
