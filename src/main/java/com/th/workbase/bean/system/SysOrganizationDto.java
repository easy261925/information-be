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

import java.util.List;

/**
 * sys_organization
 *
 * @author hut
 * @version 1.0.0 2020-04-22
 */
@ApiModel(value = "SysOrganizationDto", description = "机构对象")
@TableName("sys_organization")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = {"children", "childrenOrganizationNos", "childrenOrganizationIds"})
@KeySequence(value = "SEQ_SYS_ORGANIZATION", clazz = Integer.class)
public class SysOrganizationDto extends BaseDto implements java.io.Serializable {
    /**
     * 版本号
     */
    private static final long serialVersionUID = -8532571287000354888L;
    @ApiModelProperty(hidden = true)
    @TableId(value = "id", type = IdType.INPUT)
    private Integer id;
    /**
     * 机构统一编码
     */
    @ApiModelProperty(value = "机构统一编码", required = true, example = "12345678901234")
    @TableField(value = "organization_no")
    private String organizationNo;
    /**
     * 机构名称
     */
    @ApiModelProperty(value = "机构名称", required = true, example = "中国工商机构")
    @TableField(value = "organization_name")
    private String organizationName;
    /**
     * 机构名称
     */
    @ApiModelProperty(value = "上级机构编号", hidden = true)
    @TableField(value = "up_organization_no")
    private String upOrganizationNo;
    /**
     * 机构类型0:商业机构 1:人民机构
     */
    @ApiModelProperty(value = "机构类型0:商业机构 1:人民机构", required = true, example = "0")
    @TableField(value = "organization_type")
    private String organizationType;
    /**
     * 机构类型0:商业机构 1:人民机构
     */
    @ApiModelProperty(value = "机构类型0:总行 1:一级分行 2:二级分行 3:网点", hidden = true)
    @TableField(value = "organization_lvl")
    private Integer organizationLvl;
    /**
     * 上级id
     */
    @ApiModelProperty(value = "上级机构id", required = true, example = "0")
    @TableField(value = "up_id")
    private Integer upId;
    /**
     * 总行id
     */
    @ApiModelProperty(value = "总行id", hidden = true)
    @TableField(value = "up_lvl0")
    private String upLvl0;
    /**
     * 总行id
     */
    @ApiModelProperty(value = "一级分行id", hidden = true)
    @TableField(value = "up_lvl1")
    private String upLvl1;
    /**
     * 总行id
     */
    @ApiModelProperty(value = "二级分行id", hidden = true)
    @TableField(value = "up_lvl2")
    private String upLvl2;
    /**
     * 总行id
     */
    @ApiModelProperty(value = "网点id", hidden = true)
    @TableField(value = "up_lvl3")
    private String upLvl3;
    /**
     * 总行id
     */
    @ApiModelProperty(value = "网点id", hidden = true)
    @TableField(value = "up_lvl4")
    private String upLvl4;
    /**
     * 总行id
     */
    @ApiModelProperty(value = "网点id", hidden = true)
    @TableField(value = "up_lvl5")
    private String upLvl5;
    /**
     * 简称
     */
    @ApiModelProperty(value = "简称", example = "0")
    @TableField(value = "short_name")
    private String shortName;
    /**
     * 地址
     */
    @ApiModelProperty(value = "详细地址", example = "0")
    @TableField(value = "address")
    private String address;
    /**
     * 所在省分
     */
    @ApiModelProperty(value = "所在省分", required = true, example = "0")
    @TableField(value = "province")
    private String province;
    /**
     * 所在城市
     */
    @ApiModelProperty(value = "所在城市", required = true, example = "0")
    @TableField(value = "city")
    private String city;
    /**
     * 所在县
     */
    @ApiModelProperty(value = "所在县", required = true, example = "0")
    @TableField(value = "county")
    private String county;
    /**
     * 手机
     */
    @ApiModelProperty(value = "手机", example = "1304223122002")
    @TableField(value = "phone")
    private String phone;
    /**
     * 座机
     */
    @ApiModelProperty(value = "座机/传真", example = "123456")
    @TableField(value = "tax")
    private String tax;
    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱", example = "22002@qq.com")
    @TableField(value = "mail")
    private String mail;
    /**
     * 子对象集合
     */
    @ApiModelProperty(value = "子对象集合", hidden = true)
    @TableField(value = "children", exist = false)
    private List<SysOrganizationDto> children;
    /**
     * 备注
     */
    @ApiModelProperty(value = "子对象编号集合", hidden = true)
    @TableField(value = "childrenOrganizationNos", exist = false)
    private List<String> childrenOrganizationNos;
    /**
     * 备注
     */
    @ApiModelProperty(value = "子对象编号集合", hidden = true)
    @TableField(value = "childrenOrganizationIds", exist = false)
    private List<Integer> childrenOrganizationIds;

}
