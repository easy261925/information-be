package com.th.workbase.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysRoleDto;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hut
 * @since 2020-09-29
 */
public interface SysRoleService extends IService<SysRoleDto> {
    ResponseResultDto addRole(HttpServletRequest request, SysRoleDto sysRoleDto);

    ResponseResultDto updateRoleStatus(HttpServletRequest request, SysRoleDto sysRoleDto);

    ResponseResultDto updateRole(HttpServletRequest request, SysRoleDto sysRoleDto);

    ResponseResultDto getRoleByPage(SysRoleDto sysRoleDto, int current, int pageSize);

    ResponseResultDto deleteRole(Integer id);
}
