package com.th.workbase.common.system;


/**
 * @Author hut
 * @Description 系统统一返回的状态码
 * @Date 2019/3/18 11:07
 */

public enum ErrorEnum {
    /*
     * 错误信息
     * */
    E_401(401, "未授权"),
    E_403(403, "权限不足"),
    E_404(404, "未找到"),
    E_405(405, "请求方式有误,请检查 GET/POST"),
    E_406(406, "不接受 - 请求的格式不可得"),
    E_410(410, "已删除 - 请求的资源被永久删除，且不会再恢复"),
    E_422(422, "验证错误"),
    E_500(500, "服务器内部错误 - 服务器发生错误，请检查服务器"),
    E_502(502, "网关错误"),
    E_503(503, "服务不可用"),
    E_504(504, "网关超时"),
    E_90003(90003, "缺少必填参数");

    private Integer errorCode;

    private String errorMsg;

    ErrorEnum(Integer errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
