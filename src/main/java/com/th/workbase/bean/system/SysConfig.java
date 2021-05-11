package com.th.workbase.bean.system;

import com.baomidou.mybatisplus.annotation.*;
import com.th.workbase.bean.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author cc
 * @since 2021-02-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("SYS_CONFIG")
@ApiModel(value = "脚本配置对象", description = "")
@KeySequence(value = "SEQ_SYS_CONFIG", clazz = Integer.class)
public class SysConfig extends BaseDto {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.INPUT)
    private Integer id;

    @ApiModelProperty(value = "大车定位采集时间间隔（秒）",example = "1")
    @TableField("CARTS_CYCLE")
    private Integer cartsCycle;

    @ApiModelProperty(value = "电铲定位采集时间间隔（秒）",example = "1")
    @TableField("SHOVELS_CYCLE")
    private Integer shovelsCycle;

    @ApiModelProperty(value = "场地定位采集时间间隔（秒）",example = "1")
    @TableField("SITES_CYCLE")
    private Integer sitesCycle;

    @ApiModelProperty(value = "白班开始时间")
    @TableField("DAY_START_TIME")
    private String dayStartTime;

    @ApiModelProperty(value = "夜班开始时间")
    @TableField("NIGHT_START_TIME")
    private String nightStartTime;

    @ApiModelProperty(value = "模式选择",example = "1")
    @TableField("PUSH_MODE")
    private String pushMode;

    @ApiModelProperty(value = "南采排岩经纬度")
    @TableField("NC_LNGLAT")
    private String ncLnglat;

    @ApiModelProperty(value = "北采排岩经纬度")
    @TableField("BC_LNGLAT")
    private String bcLnglat;

    @ApiModelProperty(value = "南采区域半径")
    @TableField("NC_RADIUS")
    private Integer ncRadius;

    @ApiModelProperty(value = "北采区域半径")
    @TableField("BC_RADIUS")
    private Integer bcRadius;

    @ApiModelProperty(value = "启用电铲距离判断")
    @TableField("ENABLE_SHOVEL_DISTANCE")
    private Boolean enableShovelDistance;

    @ApiModelProperty(value = "电铲判定距离")
    @TableField("SHOVEL_DETERMINE_DISTANCE")
    private Double shovelDetermineDistance;
}
