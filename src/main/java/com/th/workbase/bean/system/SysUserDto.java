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
import java.util.Date;
import java.util.List;

/**
 * sys_user
 *
 * @author hut
 * @version 1.0.0 2020-03-27
 */
@ApiModel(value = "SysUserDto", description = "用户对象user")
@TableName("sys_user")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = {"roleDtos", "organizationIds", "organizationDto", "organizationDtos"})
@KeySequence(value = "SEQ_SYS_USER", clazz = Integer.class)
public class SysUserDto extends BaseDto implements java.io.Serializable {
    /**
     * 版本号
     */
    private static final long serialVersionUID = -6146836677369013426L;
    @ApiModelProperty(hidden = true, example = "1")
    @TableId(value = "id", type = IdType.INPUT)
    private Integer id;
    /**
     * 所属机构Id
     */
    @ApiModelProperty(value = "所属机构Id", required = true, example = "0")
    @TableField(value = "organization_id")
    private Integer organizationId;
    /**
     * 所属机构Id
     */
    @ApiModelProperty(value = "所属机构名称", hidden = true)
    @TableField(exist = false)
    private String organizationName;
    /**
     * 所属机构Id
     */
    @ApiModelProperty(value = "所属机构编号", hidden = true)
    @TableField(exist = false)
    private String organizationNo;
    @ApiModelProperty(value = "登录名", required = true)
    @TableField(value = "login_name")
    private String loginName;

    /**
     * 登录密码
     */
    @ApiModelProperty(value = "登录密码", required = true)
    @TableField(value = "login_pass")
    private String loginPass;

    @ApiModelProperty(value = "登录原始密码")
    @TableField(exist = false)
    private String originalLoginPass;

    /**
     * userType
     */
    @ApiModelProperty(value = "用户类型 1后台 2前台 3供货商", required = true, example = "1")
    @TableField(value = "user_type")
    private String userType;

    /**
     * 真实姓名
     */
    @ApiModelProperty(value = "真实姓名", required = true)
    @TableField(value = "user_name")
    private String userName;

    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称")
    @TableField(value = "nick_name")
    private String nickName;

    /**
     * 性别
     */
    @ApiModelProperty(value = "性别")
    @TableField(value = "gender")
    private String gender;

    /**
     * 手机
     */
    @ApiModelProperty(value = "手机")
    @TableField(value = "phone")
    private String phone;

    /**
     * 座机
     */
    @ApiModelProperty(value = "座机")
    @TableField(value = "tax")
    private String tax;
    /**
     * 座机
     */
    @ApiModelProperty(value = "身份证号")
    @TableField(value = "card")
    private String card;
    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    @TableField(value = "mail")
    private String mail;
    /**
     * 登录ip
     */
    @ApiModelProperty(value = "登录ip", hidden = true)
    @TableField(value = "login_ip")
    private String loginIp;
    /**
     * 最后登录时间
     */
    @ApiModelProperty(value = "最后登录时间", hidden = true)
    @TableField(value = "login_date")
    private Date loginDate;
    /**
     * mac地址
     */
    @ApiModelProperty(value = "mac地址", hidden = true)
    @TableField(value = "login_mac")
    private String loginMac;
    @ApiModelProperty(value = "角色列表")
    @TableField(value = "userRoles", exist = false)
    private List<Integer> userRoles = new ArrayList<>();
    @ApiModelProperty(value = "角色对象列表", hidden = true)
    @TableField(value = "roleDtos", exist = false)
    private List<SysRoleDto> roleDtos = new ArrayList<>();
    @ApiModelProperty(value = "机构列表", hidden = true)
    @TableField(value = "organizationIds", exist = false)
    private List<Integer> organizationIds = new ArrayList<>();
    @ApiModelProperty(value = "机构对象", hidden = true)
    @TableField(value = "organizationDto", exist = false)
    private SysOrganizationDto organizationDto;
    @ApiModelProperty(value = "机构对象集合", hidden = true)
    @TableField(value = "organizationDtos", exist = false)
    private List<SysOrganizationDto> organizationDtos;

}
