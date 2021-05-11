package com.th.workbase.bean.system;/*
import BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * sys_user
 *
 * @author hut
 * @version 1.0.0 2020-03-27
 */

import com.th.workbase.bean.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "SysLoginDto", description = "登录使用参数")
@Data
public class SysLoginDto extends BaseDto implements java.io.Serializable {
    /**
     * 版本号
     */
    private static final long serialVersionUID = -6146836677369013426L;
    /**
     * 登录名
     */
    @ApiModelProperty(value = "登录名", required = true)
    private String userName;

    /**
     * 登录密码
     */
    @ApiModelProperty(value = "登录密码", required = true)
    private String password;

    /**
     * userType
     */
    @ApiModelProperty(value = "用户类型 1后台 2前台 3供货商", required = true, example = "1")
    private String userType;

    @ApiModelProperty(value = "设备编码")
    String equipmentNo;
    @ApiModelProperty(value = "版本号")
    String versionNo;

}
