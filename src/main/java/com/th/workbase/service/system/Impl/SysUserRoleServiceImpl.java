package com.th.workbase.service.system.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysRoleDto;
import com.th.workbase.bean.system.SysUserRoleDto;
import com.th.workbase.common.utils.StringUtil;
import com.th.workbase.mapper.system.SysRoleMapper;
import com.th.workbase.mapper.system.SysUserMapper;
import com.th.workbase.mapper.system.SysUserRoleMapper;
import com.th.workbase.service.system.SysUserRoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hut
 * @since 2020-09-29
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRoleDto> implements SysUserRoleService {
    @Resource
    private SysUserMapper userMapper;
    @Resource
    private SysRoleMapper roleMapper;
    @Resource
    private SysUserRoleMapper userRoleMapper;

    @Override
    public void addUserRole(String loginName, String userType, List<Integer> roles) {
        try {
            if (roles != null && roles.size() > 0) {
                for (Integer role : roles) {
                    SysUserRoleDto userRoleDto = new SysUserRoleDto();
                    userRoleDto.setUserType(userType);
                    userRoleDto.setLoginName(loginName);
                    userRoleDto.setRoleId(role);
                    userRoleMapper.insert(userRoleDto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ResponseResultDto updateUserRole(String loginName, String userType, List<Integer> rights) {
        if (StringUtil.isNotNullOrEmpty(loginName)) {
            QueryWrapper<SysUserRoleDto> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("login_name", loginName);
            if (StringUtil.isNotNullOrEmpty(userType) && !"null".equals(userType)) {
                queryWrapper.eq("user_type", userType);
            }
            userRoleMapper.delete(queryWrapper);
            if (rights != null && rights.size() > 0) {
                for (Integer arg : rights) {
                    SysUserRoleDto addDto = new SysUserRoleDto();
                    addDto.setLoginName(loginName);
                    addDto.setUserType(userType);
                    addDto.setRoleId(arg);
                    userRoleMapper.insert(addDto);
                }
            }
            return ResponseResultDto.ok();
        } else {
            return ResponseResultDto.ServiceError("用户信息不完整");
        }
    }

    @Override
    public List<SysRoleDto> getUserRoles(String loginName, String userType) {
        QueryWrapper<SysUserRoleDto> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("login_name", loginName);
        if (StringUtil.isNotNullOrEmpty(userType) && !"null".equals(userType)) {
            queryWrapper.eq("user_type", userType);
        }
        List<SysUserRoleDto> userRoleDtos = userRoleMapper.selectList(queryWrapper);
        List<SysRoleDto> roleDtos = new ArrayList<>();
        if (userRoleDtos != null && userRoleDtos.size() > 0) {
            for (SysUserRoleDto arg : userRoleDtos) {
                SysRoleDto roleDto = roleMapper.selectById(arg.getRoleId());
                if (roleDto != null) {
                    roleDtos.add(roleDto);
                }
            }
        }
        return roleDtos;
    }

    @Override
    public List<Integer> getUserRolesStr(String loginName, String userType) {
        List<Integer> userRoles = new ArrayList<>();
        try {
            QueryWrapper<SysUserRoleDto> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("login_name", loginName);
            if (StringUtil.isNotNullOrEmpty(userType) && !"null".equals(userType)) {
                queryWrapper.eq("user_type", userType);
            }
            List<SysUserRoleDto> tmps = userRoleMapper.selectList(queryWrapper);
            if (tmps != null && tmps.size() > 0) {
                for (SysUserRoleDto arg : tmps) {
                    userRoles.add(arg.getRoleId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            userRoles = new ArrayList<>();
        }
        return userRoles;
    }

    @Override
    public ResponseResultDto deleteUserRole(String loginName, String userType) {
        QueryWrapper<SysUserRoleDto> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("login_name", loginName);
        if (StringUtil.isNotNullOrEmpty(userType) && !"null".equals(userType)) {
            queryWrapper.eq("user_type", userType);
        }
        userRoleMapper.delete(queryWrapper);
        return ResponseResultDto.ok();
    }
}
