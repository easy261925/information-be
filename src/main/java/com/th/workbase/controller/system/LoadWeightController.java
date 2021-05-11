package com.th.workbase.controller.system;


import com.th.workbase.bean.system.LoadWeightDto;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.service.system.LoadWeightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 * @author tangj
 * @since 2021-03-31
 */
@RestController
@RequestMapping("/loadWeight")
public class LoadWeightController {
    @Autowired
    LoadWeightService loadWeightService;

    @GetMapping("/loadWeight")
    public ResponseResultDto getLoadWeight() {
        return loadWeightService.getLoadWeight();
    }

    @PostMapping("/loadWeight")
    public ResponseResultDto updateLoadWeight(@RequestBody List<LoadWeightDto> loadWeightList) {
        return loadWeightService.updateLoadWeight(loadWeightList);
    }

    @DeleteMapping("/loadWeight/{id}")
    public ResponseResultDto deleteLoadWeight(@PathVariable("id") String id) {
        return loadWeightService.deleteLoadWeight(id);
    }
}

