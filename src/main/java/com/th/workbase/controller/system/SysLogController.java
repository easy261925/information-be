package com.th.workbase.controller.system;

import com.alibaba.fastjson.JSONObject;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysLogDto;
import com.th.workbase.common.system.ContentSystem;
import com.th.workbase.service.system.SysLogService;
import io.swagger.annotations.*;
import org.apache.tomcat.util.http.ResponseUtil;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@RestController
@Api(tags = {"登录接口"})
@RequestMapping("/sysLog")
public class SysLogController {
    @Resource
    private SysLogService logService;

    @ApiOperation(value = "显示字典类型列表", notes = "字典类型管理专用")
    @GetMapping(value = "/sysLog")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "第几页", dataType = "int", required = true, example = "1"),
            @ApiImplicitParam(name = "pageSize", value = "一页显示多少记录", dataType = "int", required = true, example = "20"),
            @ApiImplicitParam(name = "dtBeginDateTime", value = "开始时间", dataType = "String", example = "2020-01-01"),
            @ApiImplicitParam(name = "dtEndDateTime", value = "结束时间", dataType = "String", example = "2020-01-02"),
    })
    public ResponseResultDto getSysDictByPage(int current, int pageSize, @ApiIgnore SysLogDto sysLogDto, HttpServletResponse response) {
        return logService.getLogByPage(sysLogDto, current, pageSize);
    }
}
