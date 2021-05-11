package com.th.workbase.controller.upload;

import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.service.upload.ESRemoteUploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author cc
 * @date 2021-01-09-下午9:30
 */

@RestController
@RequestMapping("/upload")
@Api(tags = "上传文件")
public class ESRemoteUploadController {

    @Autowired
    private ESRemoteUploadService esRemoteUploadService;


    @PostMapping("/connect")
    @ApiOperation("上传文件链接地址")
    public ResponseResultDto connect() {
        return ResponseResultDto.ok();
    }

    @PostMapping("/updateEsRemoteDict")
    @ApiOperation("更新ES远程拓展字典")
    public ResponseResultDto updateEsRemoteExtDict(HttpServletRequest request) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        List<MultipartFile> attachments = multipartRequest.getFiles("attachments");
        String[] filesId = request.getParameterValues("attachments");
        esRemoteUploadService.updateEsRemoteExtDict(request, attachments, filesId);
        return ResponseResultDto.ok();
    }

    @GetMapping("/getEsRemoteDict")
    @ApiOperation("获取ES远程拓展字典")
    public ResponseResultDto getEsRemoteDict() {
        return esRemoteUploadService.getEsRemoteDict();
    }

}

