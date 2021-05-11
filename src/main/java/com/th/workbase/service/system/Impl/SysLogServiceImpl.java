package com.th.workbase.service.system.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysLogDto;
import com.th.workbase.common.utils.StringUtil;
import com.th.workbase.mapper.system.SysLogMapper;
import com.th.workbase.service.system.SysLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hut
 * @since 2020-09-29
 */
@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLogDto> implements SysLogService {
    @Resource
    private SysLogMapper logMapper;

    @Override
    public ResponseResultDto getLogByPage(SysLogDto sysLogDto, int current, int pageSize) {
        QueryWrapper<SysLogDto> queryWrapper = new QueryWrapper<>();
        if (sysLogDto.getDtEndDateTime() != null) {
            queryWrapper.le("dt_crea_date_time", StringUtil.addDays(sysLogDto.getDtEndDateTime(), 1));
        }
        if (sysLogDto.getDtBeginDateTime() != null) {
            queryWrapper.ge("dt_crea_date_time", sysLogDto.getDtBeginDateTime());
        }
        queryWrapper.orderByDesc("id");
        Page<SysLogDto> page = new Page<>(current, pageSize);
        IPage<SysLogDto> results = logMapper.selectPage(page, queryWrapper);
        return ResponseResultDto.ok().data("data", results.getRecords()).data("total", results.getTotal());
    }
}
