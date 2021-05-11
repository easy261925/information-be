package com.th.workbase.controller.system;

import com.alibaba.fastjson.JSONObject;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysOrganizationDto;
import com.th.workbase.common.system.ContentSystem;
import com.th.workbase.common.utils.StringUtil;
import com.th.workbase.config.annotation.InLogAnnotation;
import com.th.workbase.service.system.SysOrganizationService;
import io.swagger.annotations.*;
import org.apache.tomcat.util.http.ResponseUtil;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("sysOrganization")
@Api(tags = {"机构接口"})
public class SysOrganizationController {
    @Resource
    private SysOrganizationService sysOrganizationService;

    @ApiOperation(value = "新增后台机构", notes = "后台机构管理专用")
    @PostMapping("/organization")
    public ResponseResultDto addSysOrganization(@RequestBody @ApiParam(name = "机构对象", value = "传入json格式", required = true) SysOrganizationDto sysOrganizationDto, HttpServletRequest request, HttpServletResponse response) {
        return sysOrganizationService.addSysOrganization(request, sysOrganizationDto);
    }

    @ApiOperation(value = "修改后台机构", notes = "后台机构管理专用")
    @PutMapping("/organization/{id}")
    public ResponseResultDto updateSysOrganization(@RequestBody @ApiParam(name = "机构对象", value = "传入json格式", required = true) SysOrganizationDto sysOrganizationDto, @PathVariable("id") Integer id, HttpServletRequest request, HttpServletResponse response) {
        sysOrganizationDto.setId(id);
        return sysOrganizationService.updateSysOrganization(request, sysOrganizationDto);
    }

    @ApiOperation(value = "删除后台机构", notes = "后台机构管理专用")
    @DeleteMapping("/organization/{id}")
    public ResponseResultDto deleteSysOrganization(@PathVariable("id") Integer id, HttpServletResponse response) {
        return sysOrganizationService.deleteSysOrganization(id);
    }

    @ApiOperation(value = "显示后台机构列表", notes = "后台机构管理专用")
    @GetMapping(value = "/organization")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "第几页", dataType = "int", required = true, example = "1"),
            @ApiImplicitParam(name = "pageSize", value = "一页显示多少记录", dataType = "int", required = true, example = "20"),
            @ApiImplicitParam(name = "organizationName", value = "机构名", dataType = "string", example = "20"),
            @ApiImplicitParam(name = "organizationNo", value = "机构编号", dataType = "string", example = "20"),
            @ApiImplicitParam(name = "organizationType", value = "机构类型", dataType = "int", example = "1"),
            @ApiImplicitParam(name = "upId", value = "上级id", dataType = "int", example = "1"),
    })
    public ResponseResultDto getSysOrganizationByPage(HttpServletRequest request, int current, int pageSize, @ApiIgnore SysOrganizationDto sysOrganizationDto, HttpServletResponse response) {
        return sysOrganizationService.getSysOrganizationByPage(request, sysOrganizationDto, current, pageSize);
    }

    @ApiOperation(value = "显示后台机构列表", notes = "后台机构管理专用")
    @GetMapping(value = "/getTreeOrganization")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "机构id", dataType = "string", example = "1"),
    })
    public ResponseResultDto getTreeOrganization(HttpServletRequest request, String id, HttpServletResponse response) {
        SysOrganizationDto sysOrganizationDto = new SysOrganizationDto();
        if (StringUtil.isNotNullOrEmpty(id)) {
            sysOrganizationDto.setUpId(Integer.parseInt(id));
        } else {
            sysOrganizationDto.setUpId(0);
        }
        return sysOrganizationService.getTreeOrganization(request, sysOrganizationDto);
    }

}
