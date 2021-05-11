package com.th.workbase.common.commonEnum;

/**
 * @Date 2021-03-02-11:00
 * @Author tangJ
 * @Description 设备状态枚举类
 * @Version 1.0
 */
public enum EquipmentStatus {
    Offline("0"),  //离线
    Resting("1"), //休息中
    Working("2"), //工作中
    Maintenance("3"), //维修保养
    BreakDown("4")    //故障
    ;
    private String status;

    private EquipmentStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static EquipmentStatus getEquipmentStatus(String status) {
        for (EquipmentStatus equipmentStatus : values()) {
            if (equipmentStatus.getStatus().equals(status)) {
                return equipmentStatus;
            }
        }
        return null;
    }
}
