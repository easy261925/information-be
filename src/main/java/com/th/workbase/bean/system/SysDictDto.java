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
 * sys_dict
 *
 * @author hut
 * @version 1.0.0 2020-04-21
 */
@ApiModel(value = "SysDictDto", description = "字典对象")
@TableName("sys_dict")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = {"sysDictDto"})
@KeySequence(value = "SEQ_SYS_DICT", clazz = Integer.class)
public class SysDictDto extends BaseDto implements java.io.Serializable {
    /**
     * 版本号
     */
    private static final long serialVersionUID = -805253844494496971L;
    @ApiModelProperty(hidden = true,example = "1")
    @TableId(value = "id", type = IdType.ID_WORKER)
    private Integer id;
    /**
     * 字典编号
     */
    @ApiModelProperty(value = "字典编号", example = "1")
    @TableField(value = "dict_no")
    private String dictNo;
    /**
     * 字典名称
     */
    @ApiModelProperty(value = "字典名称", example = "件")
    @TableField(value = "dict_name")
    private String dictName;
    /**
     * 分组编号
     */
    @ApiModelProperty(value = "分组编号", example = "1")
    @TableField(value = "group_no")
    private String groupNo;
    /**
     * 分组编号
     */
    @ApiModelProperty(value = "字典类别", example = "0:字典分类1:字典类别")
    @TableField(value = "dict_type")
    private String dictType;
    /**
     * 分组名称
     */
    @ApiModelProperty(value = "分组名称", example = "单位")
    @TableField(value = "group_name")
    private String groupName;
    /**
     * 分组名称
     */
    @ApiModelProperty(value = "子分类", example = "单位")
    @TableField(value = "sysDictDto", exist = false)
    private List<SysDictDto> sysDictDto;
}
