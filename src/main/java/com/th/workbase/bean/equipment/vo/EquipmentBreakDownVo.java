package com.th.workbase.bean.equipment.vo;

import com.th.workbase.bean.equipment.EquipmentBreakDownDto;
import lombok.Data;

/**
 * @Date 2021-03-18-13:17
 * @Author tangJ
 * @Description 故障列表视图类
 * @Version 1.0
 */
@Data
public class EquipmentBreakDownVo extends EquipmentBreakDownDto {
    //设备名称
    private String equipmentName;
    //所属矿区
    private String oreFieldName;
    //设备类型
    private String equipmentTypeName;
    //设备子类型
    private String equipmentDetailTypeName;
    //上报人
    private String handlerName;
    //上报人电话
    private String handlerPhone;
    //信息标题
    private String title;
    //故障小类名称
    private String faultDetailName;
}
