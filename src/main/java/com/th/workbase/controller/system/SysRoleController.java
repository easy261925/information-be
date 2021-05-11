package com.th.workbase.controller.system;

import com.alibaba.fastjson.JSONObject;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysRoleDto;
import com.th.workbase.common.system.ContentSystem;
import com.th.workbase.config.annotation.InLogAnnotation;
import com.th.workbase.service.system.SysRoleService;
import io.swagger.annotations.*;
import org.apache.tomcat.util.http.ResponseUtil;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("sysRole")
@Api(tags = {"角色接口"})
public class SysRoleController {
    @Resource
    private SysRoleService roleService;

    @ApiOperation(value = "新增后台角色", notes = "后台角色管理专用")
    @PostMapping("/role")
    public ResponseResultDto addRole(@RequestBody @ApiParam(name = "角色对象", value = "传入json格式", required = true) SysRoleDto sysRoleDto, HttpServletRequest request, HttpServletResponse response) {
        return roleService.addRole(request, sysRoleDto);
    }

    @ApiOperation(value = "修改后台角色", notes = "后台角色管理专用")
    @PutMapping("/role/{id}")
    public ResponseResultDto updateRole(@RequestBody @ApiParam(name = "角色对象", value = "传入json格式", required = true) SysRoleDto sysRoleDto, @PathVariable("id") Integer id, HttpServletRequest request, HttpServletResponse response) {
        sysRoleDto.setId(id);
        return roleService.updateRole(request, sysRoleDto);
    }

    @ApiOperation(value = "修改后台角色状态", notes = "后台角色管理专用")
    @PutMapping("/roleStatus/{id}")
    public ResponseResultDto updateRoleStatus(HttpServletRequest request, @PathVariable("id") Integer id, @RequestBody @ApiParam(name = "角色对象", value = "传入json格式", required = true) SysRoleDto sysRoleDto, HttpServletResponse response) {
        sysRoleDto.setId(id);
        return roleService.updateRoleStatus(request, sysRoleDto);
    }

    @ApiOperation(value = "删除后台角色", notes = "后台角色管理专用")
    @DeleteMapping("/role/{id}")
    public ResponseResultDto deleteRole(@PathVariable("id") Integer id, HttpServletResponse response) {
        return roleService.deleteRole(id);
    }

    @ApiOperation(value = "显示后台角色列表", notes = "后台角色管理专用")
    @GetMapping(value = "/role")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "第几页", dataType = "int", required = true, example = "1"),
            @ApiImplicitParam(name = "pageSize", value = "一页显示多少记录", dataType = "int", required = true, example = "20"),
            @ApiImplicitParam(name = "roleName", value = "角色名称", dataType = "String", example = "超级管理员"),
    })
    public ResponseResultDto getRoleByPage(int current, int pageSize, @ApiIgnore SysRoleDto sysRoleDto, HttpServletResponse response) {
        return roleService.getRoleByPage(sysRoleDto, current, pageSize);
    }
}
