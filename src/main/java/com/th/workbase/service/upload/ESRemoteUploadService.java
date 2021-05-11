package com.th.workbase.service.upload;

import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysFileDto;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author cc
 * @date 2021-01-09-下午8:42
 */
public interface ESRemoteUploadService {
    SysFileDto saveFile(MultipartFile file, String path, HttpServletRequest request) throws UnsupportedEncodingException;

    boolean deleteFolder(String path);

    boolean updateEsRemoteExtDict(HttpServletRequest request, List<MultipartFile> attachments, String[] filesId);

    ResponseResultDto getEsRemoteDict();

}
