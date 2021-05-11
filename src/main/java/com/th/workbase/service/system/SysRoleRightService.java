package com.th.workbase.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysRoleRightDto;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hut
 * @since 2020-09-29
 */
public interface SysRoleRightService extends IService<SysRoleRightDto> {
    ResponseResultDto updateRoleRight(Integer role, List<String> rights, List<String> others);

    List<String> getRoleRight(Integer role, String handleType);

    List<String> getRoleRights(List<Integer> roles, String handleType);

    ResponseResultDto deleteRoleRight(Integer role);
}
