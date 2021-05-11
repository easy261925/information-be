package com.th.workbase.service.common;

import java.util.Date;

/**
 * @Date 2021-04-02-12:13
 * @Author tangJ
 * @Description 确认当前班次的服务类
 * @Version 1.0
 */
public interface DefineSwitchService {
    /**
     * 依据传入的日期时间,判断对应的日期班次信息
     *
     * @param now 需要判断的日期时间
     * @return 返回格式为 日期_班次
     */
    String defineSwitch(Date now);
}
