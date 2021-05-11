package com.th.workbase.bean.vo;

import com.th.workbase.bean.plan.PlanDetail;
import lombok.Data;

/**
 * @Date 2021-03-03-14:59
 * @Author tangJ
 * @Description 计划详细表的视图实例
 * @Version 1.0
 */
@Data
public class PlanDetailVo extends PlanDetail {
    private String categoryName;
    private Integer completed;
}
