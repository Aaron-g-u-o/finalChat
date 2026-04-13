package com.abin.mallchat.common.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.abin.mallchat.common.common.utils.AssertUtil;
import com.abin.mallchat.common.server.domain.entity.Permission;
import com.abin.mallchat.common.server.domain.vo.response.PermissionResp;
import com.abin.mallchat.common.server.mapper.PermissionMapper;
import com.abin.mallchat.common.server.service.PermissionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public List<PermissionResp> getAllPermissions() {
        List<Permission> permissions = permissionMapper.selectList(null);
        return permissions.stream()
                .map(permission -> BeanUtil.copyProperties(permission, PermissionResp.class))
                .collect(Collectors.toList());
    }

    @Override
    public PermissionResp getPermissionByCode(String code) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getCode, code);
        Permission permission = permissionMapper.selectOne(wrapper);
        AssertUtil.isNotEmpty(permission, "权限不存在");
        return BeanUtil.copyProperties(permission, PermissionResp.class);
    }
}
