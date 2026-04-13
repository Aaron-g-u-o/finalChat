package com.abin.mallchat.common.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.abin.mallchat.common.common.utils.AssertUtil;
import com.abin.mallchat.common.server.domain.entity.Role;
import com.abin.mallchat.common.server.domain.entity.Server;
import com.abin.mallchat.common.server.domain.vo.request.CreateRoleReq;
import com.abin.mallchat.common.server.domain.vo.response.RoleResp;
import com.abin.mallchat.common.server.mapper.RoleMapper;
import com.abin.mallchat.common.server.mapper.ServerMapper;
import com.abin.mallchat.common.server.service.RoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private ServerMapper serverMapper;

    @Override
    public RoleResp createRole(Long uid, CreateRoleReq req) {
        Server server = serverMapper.selectById(req.getServerId());
        AssertUtil.isNotEmpty(server, "服务器不存在");
        AssertUtil.equal(server.getCreatorId(), uid, "只有创建者才能创建角色");

        Role role = Role.builder()
                .serverId(req.getServerId())
                .name(req.getName())
                .color(req.getColor())
                .permissions(req.getPermissions())
                .createTime(new Date())
                .build();
        roleMapper.insert(role);

        return BeanUtil.copyProperties(role, RoleResp.class);
    }

    @Override
    public void deleteRole(Long uid, Long roleId) {
        Role role = roleMapper.selectById(roleId);
        AssertUtil.isNotEmpty(role, "角色不存在");

        Server server = serverMapper.selectById(role.getServerId());
        AssertUtil.isNotEmpty(server, "服务器不存在");
        AssertUtil.equal(server.getCreatorId(), uid, "只有创建者才能删除角色");

        roleMapper.deleteById(roleId);
    }

    @Override
    public RoleResp updateRole(Long uid, Long roleId, CreateRoleReq req) {
        Role role = roleMapper.selectById(roleId);
        AssertUtil.isNotEmpty(role, "角色不存在");

        Server server = serverMapper.selectById(role.getServerId());
        AssertUtil.isNotEmpty(server, "服务器不存在");
        AssertUtil.equal(server.getCreatorId(), uid, "只有创建者才能修改角色");

        role.setName(req.getName());
        role.setColor(req.getColor());
        role.setPermissions(req.getPermissions());
        roleMapper.updateById(role);

        return BeanUtil.copyProperties(role, RoleResp.class);
    }

    @Override
    public RoleResp getRole(Long roleId) {
        Role role = roleMapper.selectById(roleId);
        AssertUtil.isNotEmpty(role, "角色不存在");
        return BeanUtil.copyProperties(role, RoleResp.class);
    }

    @Override
    public List<RoleResp> getServerRoles(Long serverId) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getServerId, serverId);
        List<Role> roles = roleMapper.selectList(wrapper);

        return roles.stream()
                .map(role -> BeanUtil.copyProperties(role, RoleResp.class))
                .collect(Collectors.toList());
    }
}
