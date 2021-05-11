package com.th.workbase.controller.system;


import com.th.workbase.bean.system.LoadDistanceDto;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.service.system.LoadDistanceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author tangj
 * @since 2021-03-30
 */
@RestController
@RequestMapping("/loadDistance")
@Api(tags = {"运输距离配置"})
public class LoadDistanceController {
    @Autowired
    LoadDistanceService loadDistanceService;

    @ApiOperation(value = "获取运距信息", notes = "获取运距信息")
    @GetMapping("/loadDistance")
    public ResponseResultDto getLoadDistance(LoadDistanceDto loadDistanceParam) {
        return loadDistanceService.getLoadDistance(loadDistanceParam);
    }

    @ApiOperation(value = "更新运距信息", notes = "更新运距信息")
    @PostMapping("/loadDistance")
    public ResponseResultDto updateLoadDistance(@RequestBody List<LoadDistanceDto> loadDistanceList) {
        return loadDistanceService.updateLoadDistance(loadDistanceList);
    }

    @ApiOperation(value = "删除运距信息", notes = "删除运距信息")
    @DeleteMapping("/loadDistance/{id}")
    public ResponseResultDto deleteLoadDistance(@PathVariable("id") String id) {
        return loadDistanceService.deleteLoadDistance(id);
    }
}

