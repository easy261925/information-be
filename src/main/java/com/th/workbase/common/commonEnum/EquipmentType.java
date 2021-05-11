package com.th.workbase.common.commonEnum;

/**
 * @Date 2021-03-02-11:00
 * @Author tangJ
 * @Description 设备类型枚举类
 * @Version 1.0
 */
public enum EquipmentType {
    Shovel("0"),  //电铲
    Cart("1"), //大车
    Field("2") //场地
    ;
    private String type;

    EquipmentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
