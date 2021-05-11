package com.th.workbase.service.common.impl;

import com.th.workbase.bean.system.SysConfig;
import com.th.workbase.mapper.system.SysConfigMapper;
import com.th.workbase.service.common.DefineSwitchService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

/**
 * @Date 2021-03-03-11:56
 * @Author tangJ
 * @Description 确定班次信息
 * @Version 1.0
 */
@Service
public class DefineSwitchServiceImpl implements DefineSwitchService {
    @Resource
    SysConfigMapper configMapper;

    /**
     * 依据传入的日期时间,判断对应的日期班次信息
     *
     * @param now 需要判断的日期时间
     * @return 返回格式为 日期_班次
     */
    @Override
    public String defineSwitch(Date now) {
        try {
            SysConfig config = configMapper.selectList(null).get(0);
            LocalDate date = LocalDate.now();//取得今天的日期
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String morningShiftEndStr = date.toString() + " " + config.getNightStartTime();
            Date morningShiftEndDate = format.parse(morningShiftEndStr);
            String nightShiftEnd = date.toString() + " " + config.getDayStartTime();
            Date nightShiftEndDate = format.parse(nightShiftEnd);
            if (now.before(nightShiftEndDate)) {
                //如果在昨天夜班下班时间之前,就定义为前一天的夜班
                return date.minusDays(1).toString() + "_" + 1;
            } else if (now.before(morningShiftEndDate)) {
                //如果在今天白天下班时间之前,就定义为今天的白班
                return date.toString() + "_" + 0;
            } else {
                //否则就是今天的夜班
                return date.toString() + "_" + 1;
            }
        } catch (ParseException e) {
            return null;
        }
    }
}

