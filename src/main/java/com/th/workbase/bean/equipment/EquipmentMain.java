package com.th.workbase.bean.equipment;

import java.math.BigDecimal;
import java.util.List;

import com.baomidou.mybatisplus.annotation.*;
import com.th.workbase.bean.BaseDto;
import com.th.workbase.bean.plan.PlanTaskDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 *
 * @author cc
 * @since 2021-02-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("EQUIPMENT_MAIN")
@ApiModel(value = "EquipmentMain对象", description = "")
@KeySequence(value = "SEQ_EQUIPMENT_MAIN", clazz = Integer.class)
public class EquipmentMain extends BaseDto {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID",example = "1")
    @TableId(value = "id", type = IdType.INPUT)
    private Integer id;

    @ApiModelProperty(value = "设备编号")
    @TableField("EQUIPMENT_NO")
    private String equipmentNo;

    @ApiModelProperty(value = "设备名称")
    @TableField("EQUIPMENT_NAME")
    private String equipmentName;

    @ApiModelProperty(value = "设备类型")
    @TableField("EQUIPMENT_TYPE")
    private String equipmentType;

    @TableField("DETAIL_TYPE")
    @ApiModelProperty(value = "详细类型 equipmentType为0时 0-大铲 1-小铲 equipmentType为1时 0-A型 1-B型")
    private String detailType;

    @TableField(exist = false)
    @ApiModelProperty(value = " 详细类型名称")
    private String detailTypeName;

    @ApiModelProperty(value = "设备状态 0 - 离线 2 - 工作中 3 - 维修保养中 4 - 故障")
    @TableField("EQUIPMENT_STATUS")
    private String equipmentStatus;

    @TableField("LAT")
    private BigDecimal lat;

    @TableField("LNG")
    private BigDecimal lng;

    @TableField("REGISTERED")
    @ApiModelProperty(value = "是否注册")
    private String registered;

    @TableField("MAIN_DATA_ID")
    @ApiModelProperty(value = "其他系统设备主数据编码")
    private String mainDataId;

    @TableField("PHONE")
    @ApiModelProperty(value = "手机号码")
    private String phone;

    @TableField("DEVICE_ID")
    @ApiModelProperty(value = "设备ID")
    private String deviceId;

    @TableField("ORE_FIELD")
    @ApiModelProperty(value = "电铲所属矿区")
    private String oreField;

    @TableField(exist = false)
    @ApiModelProperty(value = "电铲所属矿区名称")
    private String oreFieldName;

    @TableField("HANDLER_ID")
    @ApiModelProperty(value = "操作人")
    private String handlerId;

    @TableField(exist = false)
    @ApiModelProperty(value = "故障类型")
    private String faultDetailType;

    @TableField(exist = false)
    @ApiModelProperty(value = "当前服务电铲名称")
    private String currentShovel;

    @TableField("APP_VERSION")
    @ApiModelProperty(value = "当前设备软件版本")
    private String appVersion;

    @TableField("PUSH_ENABLE")
    @ApiModelProperty(value = "能否推送消息")
    private String pushEnable;

    @TableField(exist = false)
    @ApiModelProperty(value = "设备对应的任务列表")
    private List<PlanTaskDto> taskList;

}
