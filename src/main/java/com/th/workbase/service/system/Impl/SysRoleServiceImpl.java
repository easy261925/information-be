package com.th.workbase.service.system.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysRoleDto;
import com.th.workbase.common.utils.StringUtil;
import com.th.workbase.mapper.system.SysRoleMapper;
import com.th.workbase.service.system.SysRoleRightService;
import com.th.workbase.service.system.SysRoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hut
 * @since 2020-09-29
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRoleDto> implements SysRoleService {
    @Resource
    private SysRoleMapper roleMapper;
    @Resource
    SysRoleRightService roleRightService;

    @Override
    public ResponseResultDto addRole(HttpServletRequest request, SysRoleDto sysRoleDto) {
        if (sysRoleDto != null) {
            sysRoleDto.setRoleNo(UUID.randomUUID().toString());
            roleMapper.insert(sysRoleDto);
            // 修改权限
            roleRightService.updateRoleRight(sysRoleDto.getId(), sysRoleDto.getRoleRights(), sysRoleDto.getAuthority());
            return ResponseResultDto.ok();
        } else {
            return ResponseResultDto.ServiceError("用户信息不完整");
        }
    }

    @Override
    public ResponseResultDto updateRole(HttpServletRequest request, SysRoleDto sysRoleDto) {
        // 更新角色信息
        roleMapper.updateById(sysRoleDto);
        // 修改权限
        roleRightService.updateRoleRight(sysRoleDto.getId(), sysRoleDto.getRoleRights(), sysRoleDto.getAuthority());
        return ResponseResultDto.ok();
    }

    @Override
    public ResponseResultDto updateRoleStatus(HttpServletRequest request, SysRoleDto sysRoleDto) {
        // 更新角色信息
        roleMapper.updateById(sysRoleDto);
        return ResponseResultDto.ok();
    }

    @Override
    public ResponseResultDto getRoleByPage(SysRoleDto sysRoleDto, int current, int pageSize) {
        QueryWrapper<SysRoleDto> queryWrapper = new QueryWrapper<>();
        if (StringUtil.isNotNullOrEmpty(sysRoleDto.getRoleName())) {
            queryWrapper.like("role_name", sysRoleDto.getRoleName());
        }
        if (StringUtil.isNotNullOrEmpty(sysRoleDto.getRemark())) {
            queryWrapper.like("remark", sysRoleDto.getRemark());
        }
        Page<SysRoleDto> page = new Page<>(current, pageSize);
        IPage<SysRoleDto> results = roleMapper.selectPage(page, queryWrapper);
        for (SysRoleDto arg : results.getRecords()) {
            arg.setRoleRights(roleRightService.getRoleRight(arg.getId(), "1"));
            arg.setAuthority(roleRightService.getRoleRight(arg.getId(), "2"));
        }
        if (sysRoleDto.getIsUse() != null) {
            queryWrapper.eq("is_use", 1);
        }
        return ResponseResultDto.ok().data("data", results.getRecords()).data("total", results.getTotal());
    }

    @Override
    public ResponseResultDto deleteRole(Integer id) {
        if (id != null) {
            roleMapper.deleteById(id);
            roleRightService.deleteRoleRight(id);
            return ResponseResultDto.ok();
        } else {
            return ResponseResultDto.ServiceError("用户信息不完整");
        }
    }
}
