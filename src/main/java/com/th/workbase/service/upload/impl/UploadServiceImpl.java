package com.th.workbase.service.upload.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.th.workbase.Exception.CustomException;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysFileDto;
import com.th.workbase.bean.system.SysUserDto;
import com.th.workbase.common.system.ErrorEnum;
import com.th.workbase.service.system.SysUserService;
import com.th.workbase.service.upload.ESRemoteUploadService;
import com.th.workbase.service.upload.SysFileService;
import com.th.workbase.service.upload.UploadService;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * @author cc
 * @date 2021-01-09-下午8:43
 */
@Service
public class UploadServiceImpl implements UploadService {
    @Resource
    private Environment env;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysFileService sysFileService;

    @Override
    //上传文件保存到本地
    public SysFileDto saveFile(MultipartFile file, String dirName, HttpServletRequest request) throws UnsupportedEncodingException {
        // 获取文件名
        String fileName = file.getOriginalFilename();
        // 获取本地文件路径
        String filePath = env.getProperty("local.uploadPath");
        // 获取文件路径
        String urlName = URLEncoder.encode(fileName, "utf-8");
        // 生成指定文件夹
        String dirUrl = filePath + dirName;
        try {
            // 创建文件夹
            File f = new File(dirUrl);
            f.mkdirs();
            //将文件保存到static文件夹里
            file.transferTo(new File(dirUrl + "/" + fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
        SysFileDto sysFileDto = new SysFileDto();
        sysFileDto.setUrl("http://" + env.getProperty("local.ip") + ":" + env.getProperty("server.port") + "/static/files/" + dirName + urlName);
        sysFileDto.setFilePath(dirUrl + fileName);
        sysFileDto.setFilename(URLDecoder.decode(fileName, "utf-8"));
        return sysFileDto;
    }

    @Override
    public ResponseResultDto saveFiles(Map<String, Object> map) {
        System.out.println(map);
        return ResponseResultDto.ok();
    }

}
