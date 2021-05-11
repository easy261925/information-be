package com.th.workbase.service;

import com.th.workbase.bean.Files;
import com.baomidou.mybatisplus.extension.service.IService;
import com.th.workbase.bean.system.ResponseResultDto;

import java.util.HashMap;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author cc
 * @since 2021-05-11
 */
public interface FilesService extends IService<Files> {

    ResponseResultDto create(HashMap<String, Object> map);

    ResponseResultDto getData(Files file, int current, int pageSize);
}
