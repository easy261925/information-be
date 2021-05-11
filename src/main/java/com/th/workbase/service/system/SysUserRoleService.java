package com.th.workbase.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysRoleDto;
import com.th.workbase.bean.system.SysUserRoleDto;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hut
 * @since 2020-09-29
 */
public interface SysUserRoleService extends IService<SysUserRoleDto> {
    void addUserRole(String loginName, String userType, List<Integer> roles);

    ResponseResultDto updateUserRole(String loginName, String userType, List<Integer> rights);

    ResponseResultDto deleteUserRole(String loginName, String userType);

    List<SysRoleDto> getUserRoles(String loginName, String userType);

    List<Integer> getUserRolesStr(String loginName, String userType);

}
