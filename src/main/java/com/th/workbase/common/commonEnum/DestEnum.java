package com.th.workbase.common.commonEnum;

import lombok.Data;

/**
 * @author cc
 * @date 2021-02-26-下午3:41
 */

public enum DestEnum {
    KP_1("0", "矿破1#"),
    KP_2("1", "矿破2#"),
    KP_3("2", "矿破3#"),
    YP_1("3", "矿破2#");

    private String code;

    private String msg;

    DestEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
