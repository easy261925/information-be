package com.th.workbase.bean.system;/*
 * Welcome to use the TableGo Tools.
 *
 * http://www.tablego.cn
 *
 * http://vipbooks.iteye.com
 * http://blog.csdn.net/vipbooks
 * http://www.cnblogs.com/vipbooks
 *
 * Author: bianj
 * Email: tablego@qq.com
 * Version: 6.8.0
 */

import com.baomidou.mybatisplus.annotation.*;
import com.th.workbase.bean.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * sys_organization
 *
 * @author hut
 * @version 1.0.0 2020-04-22
 */
@ApiModel(value = "SysLogDto", description = "系统日志")
@TableName("sys_log")
@Data
@KeySequence(value = "SEQ_SYS_LOG", clazz = Integer.class)
public class SysLogDto extends BaseDto implements java.io.Serializable {
    /**
     * 版本号
     */
    private static final long serialVersionUID = -8532571287000354888L;
    @ApiModelProperty(hidden = true,example = "1")
    @TableId(value = "id", type = IdType.INPUT)
    private Integer id;
    /**
     * 机构统一编码
     */
    @ApiModelProperty(value = "模块id", example = "sys_organization")
    @TableField(value = "mode_type")
    private String modeType;
    /**
     * 机构名称
     */
    @ApiModelProperty(value = "模块名称", example = "机构管理")
    @TableField(value = "mode_name")
    private String modeName;
    /**
     * 机构名称
     */
    @ApiModelProperty(value = "日志信息", hidden = true)
    @TableField(value = "info")
    private String info;
    /**
     * 机构名称
     */
    @ApiModelProperty(value = "日志信息", hidden = true)
    @TableField(value = "oper_user")
    private String operUser;
    /**
     * 机构名称
     */
    @ApiModelProperty(value = "日志信息", hidden = true)
    @TableField(value = "oper_ip")
    private String operIp;

}
