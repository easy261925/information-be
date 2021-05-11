package com.th.workbase.common.commonEnum;

/**
 * @Date 2021-03-02-11:00
 * @Author tangJ
 * @Description 大车详细类型枚举类
 * @Version 1.0
 */
public enum CartDetailType {
    Volvo("0"), //沃尔沃
    Cat("1"),  //卡特
    Tr100("2") ,//TR100
    Xs("3") //小松
    ;
    private String type;

    CartDetailType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
