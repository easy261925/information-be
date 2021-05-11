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
import springfox.documentation.annotations.ApiIgnore;

/**
 * sys_user_role
 *
 * @author hut
 * @version 1.0.0 2020-03-27
 */
@ApiModel(value = "SysUserRoleDto", description = "用户角色对象")
@ApiIgnore
@TableName("sys_user_role")
@Data
@KeySequence(value = "SEQ_SYS_USER_ROLE", clazz = Integer.class)
public class SysUserRoleDto extends BaseDto implements java.io.Serializable {
    /**
     * 版本号
     */
    private static final long serialVersionUID = 1012022187250451907L;
    @ApiModelProperty(hidden = true,example = "1")
    @TableId(value = "id", type = IdType.INPUT)
    private Integer id;
    /**
     * 角色编号
     */
    @ApiModelProperty(value = "角色id", required = true, example = "1")
    @TableField(value = "role_id")
    private Integer roleId;
    /**
     * 角色名称
     */
    @ApiModelProperty(value = "登录名", required = true, example = "1")
    @TableField(value = "login_name")
    private String loginName;
    /**
     * 角色名称
     */
    @ApiModelProperty(value = "用户类型", required = true, example = "1")
    @TableField(value = "user_type")
    private String userType;
}
