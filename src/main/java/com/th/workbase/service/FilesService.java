package com.th.workbase.service;

import com.th.workbase.bean.Files;
import com.baomidou.mybatisplus.extension.service.IService;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysFileDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
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

    ResponseResultDto downloadFiles(String id);

    String getFilesDirName(Files files);

    ResponseResultDto deleteFiles(String id);

    ResponseResultDto deleteAllFiles(Files Files);
}
