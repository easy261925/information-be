package com.th.workbase.service.upload;

import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysFileDto;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * @author cc
 * @date 2021-01-09-下午8:42
 */
public interface UploadService {
    SysFileDto saveFile(MultipartFile file, String path, HttpServletRequest request) throws UnsupportedEncodingException;

    ResponseResultDto saveFiles(Map<String, Object> map);
}
