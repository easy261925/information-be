package com.th.workbase.common.commonEnum;

/**
 * @Date 2021-03-02-11:00
 * @Author tangJ
 * @Description 故障类型枚举类
 * @Version 1.0
 */
public enum FaultDetailType {
    AAA("0"),  //#todo 等甲方确定了细类之后再进行定义
    BBB("1")
    ;
    private String type;

    FaultDetailType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
