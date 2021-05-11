package com.th.workbase.controller.system;

import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysUserDto;
import com.th.workbase.service.system.SysUserService;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("sysUser")
@Api(tags = {"用户接口"})
public class SysUserController {
    @Resource
    private SysUserService userService;

    @ApiOperation(value = "新增后台用户", notes = "后台用户管理专用")
    @PostMapping("/account")
    public ResponseResultDto addUser(@RequestBody @ApiParam(name = "用户对象", value = "传入json格式", required = true) SysUserDto sysUserDto, HttpServletRequest request, HttpServletResponse response) {
        return userService.addUser(request, sysUserDto);
    }

    @ApiOperation(value = "修改后台用户", notes = "后台用户管理专用")
    @PutMapping("/account/{id}")
    public ResponseResultDto updateUser(@RequestBody @ApiParam(name = "用户对象", value = "传入json格式", required = true) SysUserDto sysUserDto, @PathVariable("id") Integer id, HttpServletRequest request, HttpServletResponse response) {
        sysUserDto.setId(id);
        return userService.updateUser(request, sysUserDto);
    }

    @ApiOperation(value = "删除后台用户", notes = "后台用户管理专用")
    @DeleteMapping("/account/{id}")
    public ResponseResultDto deleteUser(@PathVariable("id") Integer id, HttpServletResponse response) {
        return userService.deleteUser(id);
    }

    @ApiOperation(value = "获取当前登录用户信息", notes = "后台用户管理专用")
    @PostMapping(value = "/getUserInfo")
    public ResponseResultDto getUserInfo(HttpServletRequest request, HttpServletResponse response) {
        return userService.getUserInfo(request);
    }

    @ApiOperation(value = "修改用户密码", notes = "修改用户密码")
    @PutMapping(value = "/changePassword")
    public ResponseResultDto changePassword(@RequestBody SysUserDto sysUserDto) {
        return userService.changePassword(sysUserDto);
    }

    @ApiOperation(value = "重置用户密码", notes = "重置用户密码")
    @PutMapping(value = "/resetPassword/{id}")
    public ResponseResultDto resetPassword(@PathVariable("id") String id) {
        return userService.resetPassword(id);
    }

    @ApiOperation(value = "显示后台用户列表", notes = "后台用户管理专用")
    @GetMapping(value = "/account")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "第几页", dataType = "int", required = true, example = "1"),
            @ApiImplicitParam(name = "pageSize", value = "一页显示多少记录", dataType = "int", required = true, example = "20"),
            @ApiImplicitParam(name = "loginName", value = "登录名", dataType = "string", example = "11"),
            @ApiImplicitParam(name = "userName", value = "用户名", dataType = "string", example = "20"),
            @ApiImplicitParam(name = "mail", value = "邮箱", dataType = "string", example = "20"),

    })
    public ResponseResultDto getUserByPage(HttpServletRequest request, int current, int pageSize, @ApiIgnore SysUserDto sysUserDto, HttpServletResponse response) {
        return userService.getUserByPage(request, sysUserDto, current, pageSize);
    }
}
