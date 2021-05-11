package com.th.workbase.bean.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.th.workbase.bean.BaseDto;
import com.th.workbase.bean.plan.PlanDetail;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author cc
 * @date 2021-02-26-下午1:53
 */
@Data
public class PlanVo extends BaseDto {
    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.INPUT)
    private Integer id;

    @ApiModelProperty(value = "目的地ID")
    private String destination;

    @ApiModelProperty(value = "排班日期")
    private String scheduleDate;

    @ApiModelProperty(value = "排班产量")
    private String amount;

    @ApiModelProperty(value = "班次类型 0-白班 1-夜班")
    private String shiftType;

    @ApiModelProperty(value = "详细计划")
    private List<PlanDetail> planDetails;
}
