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

import com.th.workbase.bean.BaseDto;
import lombok.Data;

/**
 * sys_organization
 *
 * @author hut
 * @version 1.0.0 2020-04-22
 */
@Data
public class SysOrganizationTreeDto extends BaseDto implements java.io.Serializable {
    /**
     * 版本号
     */
    private static final long serialVersionUID = -8532571287000354888L;

    /**
     * 机构统一编码
     */
    private Integer id;

    /**
     * 机构名称
     */
    private String title;

    /**
     * 机构类型0:商业机构 1:人民机构
     */
    private String value;

    /**
     * 上级id
     */
    private Integer pId;

}
