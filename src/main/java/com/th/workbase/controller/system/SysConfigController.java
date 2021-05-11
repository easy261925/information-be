package com.th.workbase.controller.system;


import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysConfig;
import com.th.workbase.service.system.SysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author cc
 * @since 2021-02-25
 */
@RestController
@RequestMapping("/sysConfig")
@Api(tags = {"系统配置"})
public class SysConfigController {

    @Autowired
    private SysConfigService sysConfigService;

    @ApiOperation(value = "获取系统脚本配置", notes = "获取系统脚本配置")
    @GetMapping("/sysConfig")
    public ResponseResultDto getSysConfig() {
        return sysConfigService.getSysConfig();
    }

    @ApiOperation(value = "更新系统脚本配置", notes = "更新系统脚本配置")
    @PutMapping("/sysConfig")
    public ResponseResultDto updateSysConfig(@RequestBody @ApiParam(name = "脚本配置对象", value = "传入json格式", required = true) SysConfig sysConfig, HttpServletRequest request, HttpServletResponse response) {
        return sysConfigService.updateSysConfig(request, sysConfig);
    }


}

