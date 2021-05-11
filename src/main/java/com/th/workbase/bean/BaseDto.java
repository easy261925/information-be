package com.th.workbase.bean;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@ToString(exclude = {"dtCreaDateTime", "dtUpdateDateTime", "current", "pageSize", "take_num", "skip_num", "limit", "version", "isDel"})
public class BaseDto implements java.io.Serializable {
    @ApiModelProperty(hidden = true)
    @TableField(value = "dt_crea_date_time", fill = FieldFill.INSERT)
    private Date dtCreaDateTime;

    @ApiModelProperty(hidden = true)
    @TableField(value = "dt_update_date_time", fill = FieldFill.INSERT_UPDATE)
    private Date dtUpdateDateTime;

//    @Version
    private Integer version;

    @TableLogic
    private String isDel;

    @ApiModelProperty(value = "是否启用 0:停用 1:启用", example = "1")
    @TableField(value = "is_use")
    private Integer isUse;

    @ApiModelProperty(value = "第几页", hidden = true, example = "1")
    @TableField(value = "current", exist = false)
    private Integer current;

    @ApiModelProperty(value = "一页显示多少记录", hidden = true, example = "20")
    @TableField(value = "page_size", exist = false)
    private Integer pageSize;

    @ApiModelProperty(hidden = true,example = "1")
    @TableField(value = "take_num", exist = false)
    private Integer take_num;// 每页取记录数

    @ApiModelProperty(hidden = true,example = "1")
    @TableField(value = "skip_num", exist = false)
    private Integer skip_num;// 跳过记录数

    @ApiModelProperty(hidden = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @TableField(value = "dt_begin_date_time", exist = false)
    private Date dtBeginDateTime;// 查询开始时间，用于页面接收

    @ApiModelProperty(hidden = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @TableField(value = "dt_end_date_time", exist = false)
    private Date dtEndDateTime;// 查询截止时间，用于页面接收

    @ApiModelProperty(hidden = true,example = "1")
    @TableField(value = "limit", exist = false)
    private Integer limit;// 获取多少记录，mysql数据库专用
    /**
     * 备注
     */
    @ApiModelProperty("备注")
    @TableField(value = "remark", exist = false)
    private String remark;
    /**
     * 显示顺序
     */
    @ApiModelProperty(value = "显示顺序", hidden = true,example = "1")
    @TableField(value = "show_order", exist = false)
    private Integer showOrder;


}
