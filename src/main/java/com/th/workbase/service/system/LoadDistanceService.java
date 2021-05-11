package com.th.workbase.service.system;

import com.th.workbase.bean.system.LoadDistanceDto;
import com.baomidou.mybatisplus.extension.service.IService;
import com.th.workbase.bean.system.ResponseResultDto;

import java.util.List;

/**
 * @author tangj
 * @since 2021-03-30
 */
public interface LoadDistanceService extends IService<LoadDistanceDto> {

    ResponseResultDto getLoadDistance(LoadDistanceDto loadDistanceParam);

    ResponseResultDto updateLoadDistance(List<LoadDistanceDto> loadDistanceList);

    ResponseResultDto deleteLoadDistance(String id);
}
