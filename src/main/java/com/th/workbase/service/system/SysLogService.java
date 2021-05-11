package com.th.workbase.service.system;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysLogDto;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hut
 * @since 2020-09-29
 */
public interface SysLogService extends IService<SysLogDto> {
    ResponseResultDto getLogByPage(SysLogDto sysLogDto, int current, int pageSize);
}
