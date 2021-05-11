package com.th.workbase.controller.upload;

import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.service.upload.ESRemoteUploadService;
import com.th.workbase.service.upload.UploadService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cc
 * @date 2021-01-09-下午9:30
 */

@RestController
@RequestMapping("/create")
@Api(tags = "上传文件")
public class UploadController {

    @Autowired
    private UploadService uploadService;

    @PostMapping("/test")
    @ApiOperation("上传文件链接地址")
    public ResponseResultDto connect() {
        return ResponseResultDto.ok();
    }
}

