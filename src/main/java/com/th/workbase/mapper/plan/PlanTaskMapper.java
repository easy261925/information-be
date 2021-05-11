package com.th.workbase.mapper.plan;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.th.workbase.bean.plan.PlanTaskDto;

/**
 *
 * @author tangj
 * @since 2021-03-02
 */
public interface PlanTaskMapper extends BaseMapper<PlanTaskDto> {

    void setTaskToTmp(String taskId);
}
