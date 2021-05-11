package com.th.workbase.common.commonEnum;

/**
 * @Date 2021-03-05-7:43
 * @Author tangJ
 * @Description 推送消息类型
 * @Version 1.0
 */
public enum MessageType {
    System("0"),
    Business("1")
    ;
    private String type;
    MessageType(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
