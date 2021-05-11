package com.th.workbase.bean.equipment.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Date 2021-04-01-7:46
 * @Author tangJ
 * @Description 统计的视图类
 * @Version 1.0
 */
@Data
public class StatisticVo {
    @ApiModelProperty(value = "前台区分数据id")
    private Integer id;

    @ApiModelProperty(value = "电铲编码")
    private String shovelNo;

    @ApiModelProperty(value = "电铲名称")
    private String shovelName;

    @ApiModelProperty(value = "电铲类型")
    private String shovelType;

    @ApiModelProperty(value = "大车编码")
    private String cartNo;

    @ApiModelProperty(value = "大车名称")
    private String cartName;

    @ApiModelProperty(value = "大车类型")
    private String cartType;

    @ApiModelProperty(value = "大车类型名称")
    private String cartTypeName;

    @ApiModelProperty(value = "拉矿距离", example = "1")
    private Integer mineDistance;

    @ApiModelProperty(value = "拉岩石距离", example = "1")
    private Integer rockDistance;

    @ApiModelProperty(value = "拉矿石车数", example = "1")
    private Integer mineCount;

    @ApiModelProperty(value = "拉岩石车数", example = "1")
    private Integer rockCount;

    @ApiModelProperty(value = "岩石吨数", example = "1")
    private Integer rockTons;

    @ApiModelProperty(value = "矿石吨数", example = "1")
    private Integer mineTons;

    @ApiModelProperty(value = "岩石周转率", example = "1")
    private Double rockTurnOver;

    @ApiModelProperty(value = "矿石周转率")
    private Double mineTurnOver;

    @ApiModelProperty(value = "开始日期")
    private String startDate;

    @ApiModelProperty(value = "结束日期")
    private String endDate;

    @ApiModelProperty(value = "班次")
    private String shiftType;

    @ApiModelProperty(value = "日期")
    private String scheduleDate;

    @ApiModelProperty(value = "司机姓名")
    private String handlerName;

    @ApiModelProperty(value = "司机id")
    private String handlerId;
}
