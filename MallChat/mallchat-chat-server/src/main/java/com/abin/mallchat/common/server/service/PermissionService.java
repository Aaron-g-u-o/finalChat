package com.abin.mallchat.common.server.service;

import com.abin.mallchat.common.server.domain.vo.response.PermissionResp;

import java.util.List;

public interface PermissionService {

    List<PermissionResp> getAllPermissions();

    PermissionResp getPermissionByCode(String code);
}
