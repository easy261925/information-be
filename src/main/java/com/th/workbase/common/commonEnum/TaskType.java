package com.th.workbase.common.commonEnum;

/**
 * @Date 2021-03-02-11:00
 * @Author tangJ
 * @Description 任务类型枚举类
 * @Version 1.0
 */
public enum TaskType {
    Normal("0"),  //普通任务
    Temp("1"), //临时任务
    System("2")//系统任务
    ;
    private String type;

    TaskType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
