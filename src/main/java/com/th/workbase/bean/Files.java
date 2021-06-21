package com.th.workbase.bean;

import java.math.BigDecimal;

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
 * @since 2021-05-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("FILES")
@ApiModel(value = "Files对象", description = "")
@KeySequence(value = "SEQ_FILES", clazz = Integer.class)
public class Files extends BaseDto {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.INPUT)
    private Integer id;

    @ApiModelProperty(value = "WORD文件地址")
    @TableField("WORD_URL")
    private String wordUrl;

    @ApiModelProperty(value = "是否转换过word")
    @TableField("TRANSFER")
    private String transfer;

    @ApiModelProperty(value = "文件夹名称")
    @TableField("DIR_NAME")
    private String dirName;

    @ApiModelProperty(value = "压缩包名称")
    @TableField("ZIP_NAME")
    private String zipName;

    @ApiModelProperty(value = "是否压缩了图片")
    @TableField("ZIP_IMAGES")
    private String zipImages;

    @ApiModelProperty(value = "镇ID")
    @TableField("TOWN_ID")
    private String townId;

    @ApiModelProperty(value = "村名")
    @TableField("VILLAGE_NAME")
    private String villageName;

    @ApiModelProperty(value = "户主姓名")
    @TableField("USERNAME")
    private String username;

    @ApiModelProperty(value = "户主电话")
    @TableField("PHONE")
    private String phone;

    @ApiModelProperty(value = "户主身份证正面")
    @TableField("IDA")
    private String ida;

    @ApiModelProperty(value = "户主身份证背面")
    @TableField("IDB")
    private String idb;

    @ApiModelProperty(value = "户口本ID")
    @TableField("HKB")
    private String hkb;

    @ApiModelProperty(value = "房屋产权证")
    @TableField("FWCQZ")
    private String fwcqz;

    @ApiModelProperty(value = "土地使用证")
    @TableField("TDSYZ")
    private String tdsyz;

    @ApiModelProperty(value = "其他权属证明材料")
    @TableField("QTQSZM")
    private String qtqszm;

    @ApiModelProperty(value = "其他材料")
    @TableField("QTCL")
    private String qtcl;

    @ApiModelProperty(value = "房屋持有人1姓名")
    @TableField("USERNAMEA1")
    private String usernameA1;

    @ApiModelProperty(value = "房屋持有人1电话")
    @TableField("PHONEA1")
    private String phoneA1;

    @ApiModelProperty(value = "房屋持有人2姓名")
    @TableField("USERNAMEA2")
    private String usernameA2;

    @ApiModelProperty(value = "房屋持有人2电话")
    @TableField("PHONEA2")
    private String phoneA2;

    @ApiModelProperty(value = "房屋持有人1身份证正面")
    @TableField("IDA1")
    private String ida1;

    @ApiModelProperty(value = "房屋持有人1身份证背面")
    @TableField("IDB1")
    private String idb1;

    @ApiModelProperty(value = "房屋持有人2身份证正面")
    @TableField("IDA2")
    private String ida2;

    @ApiModelProperty(value = "房屋持有人2身份证背面")
    @TableField("IDB2")
    private String idb2;

    @ApiModelProperty(value = "户口本信息")
    @TableField("HKB1")
    private String hkb1;

    @ApiModelProperty(value = "房屋产权来源证明")
    @TableField("FWCQLY")
    private String fwcqly;


}
