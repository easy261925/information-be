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

/**
 * sys_role_right
 *
 * @author hut
 * @version 1.0.0 2020-03-27
 */
@ApiModel(value = "SysRoleRightDto", description = "角色权限对象")
@TableName("sys_role_right")
@Data
@KeySequence(value = "SEQ_SYS_ROLE_RIGHT", clazz = Integer.class)
public class SysRoleRightDto extends BaseDto implements java.io.Serializable {
    /**
     * 版本号
     */
    private static final long serialVersionUID = 7555208762631564161L;
    @ApiModelProperty(hidden = true,example = "1")
    @TableId(value = "id", type = IdType.INPUT)
    private Integer id;
    /**
     * 角色编号
     */
    @ApiModelProperty(value = "角色Id", required = true, example = "1")
    @TableField(value = "role_id")
    private Integer roleId;
    /**
     * 菜单名称
     */
    @ApiModelProperty(value = "菜单")
    @TableField(value = "menu_no")
    private String menuNo;
    /**
     * 权限类型1菜单权限 2 按钮权限 3数据权限
     */
    @ApiModelProperty(value = "权限属性", hidden = true)
    @TableField(value = "handle_type")
    private String handleType;


}
