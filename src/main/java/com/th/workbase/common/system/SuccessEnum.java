package com.th.workbase.common.system;


/**
 * @Author hut
 * @Description 系统统一返回的状态码
 * @Date 2019/3/18 11:07
 */

public enum SuccessEnum {
    /*
     * 错误信息
     * */
    S_200(200, "访问成功");

    private Integer successCode;

    private String successMsg;

    SuccessEnum(Integer successCode, String successMsg) {
        this.successCode = successCode;
        this.successMsg = successMsg;
    }

    public Integer getSuccessCode() {
        return successCode;
    }

    public void setSuccessCode(Integer successCode) {
        this.successCode = successCode;
    }

    public String getSuccessMsg() {
        return successMsg;
    }

    public void setSuccessMsg(String successMsg) {
        this.successMsg = successMsg;
    }
}
