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
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * sys_role
 *
 * @author hut
 * @version 1.0.0 2020-03-27
 */
@ApiModel(value = "SysRoleDto", description = "用户角色")
@TableName("sys_role")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@KeySequence(value = "SEQ_SYS_ROLE", clazz = Integer.class)
public class SysRoleDto extends BaseDto implements java.io.Serializable {
    /**
     * 版本号
     */
    private static final long serialVersionUID = -2480073973841361766L;
    @ApiModelProperty(hidden = true,example = "1")
    @TableId(value = "id", type = IdType.INPUT)
    private Integer id;
    /**
     * 角色编号
     */
    @ApiModelProperty(value = "角色编号", hidden = true)
    @TableField(value = "role_no")
    private String roleNo;
    /**
     * 角色编号
     */
    @ApiModelProperty(value = "类型0:无角色(管理员角色)", hidden = true)
    @TableField(value = "role_type")
    private String roleType;
    /**
     * 角色名称
     */
    @ApiModelProperty(value = "角色名称", required = true, example = "超级管理员")
    @TableField(value = "role_name")
    private String roleName;

    @ApiModelProperty(value = "权限列表")
    @TableField(value = "roleRights", exist = false)
    private List<String> roleRights = new ArrayList<>();
    @ApiModelProperty(value = "权限回显")
    @TableField(value = "authority", exist = false)
    private List<String> authority = new ArrayList<>();

}
