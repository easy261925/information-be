package com.th.workbase.bean.system;

import com.baomidou.mybatisplus.annotation.*;
import com.th.workbase.bean.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author cc
 * @since 2021-01-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("SYS_FILE")
@ApiModel(value = "SysFile对象", description = "")
@KeySequence(value = "SEQ_SYS_FILE", clazz = Integer.class)
public class SysFileDto extends BaseDto implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(hidden = true,example = "1")
    @TableId(value = "ID", type = IdType.INPUT)
    private Integer id;

    @ApiModelProperty(value = "文件磁盘路径")
    @TableField("FILE_PATH")
    private String filePath;

    @ApiModelProperty(value = "文件访问路径")
    @TableField("URL")
    private String url;

    @ApiModelProperty(value = "文件名称")
    @TableField("FILENAME")
    private String filename;

    @ApiModelProperty(value = "文件类型")
    @TableField("TYPE")
    private String type;

    @ApiModelProperty(value = "外键ID")
    @TableField("PK_VALUE")
    private String pkValue;

    @ApiModelProperty(value = "上传者 ID",example = "1")
    @TableField("HANDLER_ID")
    private Integer handlerId;

}
