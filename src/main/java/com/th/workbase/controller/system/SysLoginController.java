package com.th.workbase.controller.system;

import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysLoginDto;
import com.th.workbase.service.system.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@Api(tags = {"登录接口"})
@RequestMapping("/login")
public class SysLoginController {
    @Resource
    private SysUserService userService;

    @ApiOperation(value = "登录接口", notes = "登录接口")
    @PostMapping("/login")
    public ResponseResultDto login(@ApiParam(name = "loginDto用户登录model", value = "登录名", required = true) @RequestBody SysLoginDto loginDto, HttpServletRequest request, HttpServletResponse response) {
        return userService.login(request, loginDto.getUserName(), loginDto.getPassword(),loginDto.getEquipmentNo(),loginDto.getVersionNo());
    }

    @ApiOperation(value = "登出接口", notes = "登出接口")
    @PostMapping("/logout")
    public ResponseResultDto logout(HttpServletRequest request, HttpServletResponse response) {
        return userService.logout(request);
    }
}
