package com.th.workbase.bean.system;

import com.baomidou.mybatisplus.annotation.*;
import com.th.workbase.bean.BaseDto;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author tangj
 * @since 2021-03-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("VERSION_MANAGER")
@ApiModel(value = "VersionManager对象")
@KeySequence(value = "SEQ_VERSION_MANAGER", clazz = Integer.class)
public class VersionManager extends BaseDto {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    private Integer id;

    @TableField("APP_VERSION")
    private String appVersion;

    @TableField("FILE_LOCATION")
    private String fileLocation;

    @TableField("REMARK")
    private String remark;

    @TableField("ORIGINAL_FILE_NAME")
    private String originalFileName;

    @TableField("APK_MD5")
    private String apkMd5;

    @TableField("APK_SIZE")
    private Long apkSize;

    @TableField("FORCE_UPDATE")
    private Integer forceUpdate;

    @TableField(exist = false)
    private Integer installCount;

    @TableField(exist = false)
    private String downloadUrl;

}
