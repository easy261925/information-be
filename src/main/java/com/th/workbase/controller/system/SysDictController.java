package com.th.workbase.controller.system;

import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysDictDto;
import com.th.workbase.config.annotation.InLogAnnotation;
import com.th.workbase.service.system.SysDictService;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("sysDict")
@Api(tags = {"字典接口"})
public class SysDictController {
    @Resource
    private SysDictService sysDictService;

    @ApiOperation(value = "新增字典类型", notes = "字典类型管理专用")
    @PostMapping("/sysDict")
    public ResponseResultDto addSysDict(@RequestBody @ApiParam(name = "字典类型对象", value = "传入json格式", required = true) SysDictDto sysDictDto, HttpServletResponse response) {
        return sysDictService.addSysDict(sysDictDto);
    }

    @ApiOperation(value = "修改字典类型", notes = "字典类型管理专用")
    @PutMapping("/sysDict/{id}")
    public ResponseResultDto updateSysDict(@RequestBody @ApiParam(name = "字典类型对象", value = "传入json格式", required = true) SysDictDto sysDictDto, @PathVariable("id") Integer id, HttpServletResponse response) {
        sysDictDto.setId(id);
        return sysDictService.updateSysDict(sysDictDto);
    }

    @ApiOperation(value = "删除字典类型", notes = "字典类型管理专用")
    @DeleteMapping("/sysDict/{id}")
    public ResponseResultDto deleteSysDict(@PathVariable("id") Integer id, HttpServletResponse response) {
        return sysDictService.deleteSysDict(id);
    }

    @ApiOperation(value = "显示字典类型列表", notes = "字典类型管理专用")
    @GetMapping(value = "/sysDict")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "第几页", dataType = "int", required = true, example = "1"),
            @ApiImplicitParam(name = "pageSize", value = "一页显示多少记录", dataType = "int", required = true, example = "20"),
            @ApiImplicitParam(name = "groupNo", value = "字典类型编号", dataType = "String", example = "单位"),
            @ApiImplicitParam(name = "groupName", value = "字典类型名称", dataType = "String", example = "单位"),
            @ApiImplicitParam(name = "dictNo", value = "字典元素编号", dataType = "String", example = "单位"),
            @ApiImplicitParam(name = "dictName", value = "字典元素编号", dataType = "String", example = "单位"),
    })
    public ResponseResultDto getSysDictByPage(int current, int pageSize, @ApiIgnore SysDictDto sysDictDto, HttpServletResponse response) {
        return sysDictService.getSysDictByPage(sysDictDto, current, pageSize);
    }
}
