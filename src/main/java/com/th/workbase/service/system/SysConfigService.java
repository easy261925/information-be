package com.th.workbase.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysConfig;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author cc
 * @since 2021-02-25
 */
public interface SysConfigService extends IService<SysConfig> {

    ResponseResultDto getSysConfig();

    ResponseResultDto updateSysConfig(HttpServletRequest request, SysConfig sysConfig);


}
