package com.th.workbase.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysOrganizationDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface SysOrganizationService extends IService<SysOrganizationDto> {
    ResponseResultDto addSysOrganization(HttpServletRequest request, SysOrganizationDto sysOrganizationDto);

    ResponseResultDto updateSysOrganization(HttpServletRequest request, SysOrganizationDto sysOrganizationDto);

    List<Integer> getSysOrganizationIdsByLoginName(String loginName, String userType);

    ResponseResultDto getSysOrganizationByPage(HttpServletRequest request, SysOrganizationDto sysOrganizationDto, int current, int pageSize);

    ResponseResultDto getTreeOrganization(HttpServletRequest request, SysOrganizationDto sysOrganizationDto);

    ResponseResultDto deleteSysOrganization(Integer id);
}
