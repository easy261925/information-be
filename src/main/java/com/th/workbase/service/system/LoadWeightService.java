package com.th.workbase.service.system;

import com.th.workbase.bean.system.LoadWeightDto;
import com.baomidou.mybatisplus.extension.service.IService;
import com.th.workbase.bean.system.ResponseResultDto;

import java.util.List;

/**
 *
 * @author tangj
 * @since 2021-03-31
 */
public interface LoadWeightService extends IService<LoadWeightDto> {

    ResponseResultDto getLoadWeight();

    ResponseResultDto deleteLoadWeight(String id);

    ResponseResultDto updateLoadWeight(List<LoadWeightDto> loadWeightList);
}
