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
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * @author cc
 * @date 2021-01-09-下午8:43
 */
@Service
public class ESRemoteUploadServiceImpl implements ESRemoteUploadService {
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
    public boolean deleteFolder(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        if (file.isFile()) {
            return file.delete();
        }
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isFile()) {
                if (!f.delete()) {
                    System.out.println(f.getAbsolutePath() + " delete error!");
                    return false;
                }
            } else {
                if (!this.deleteFolder(f.getAbsolutePath())) {
                    return false;
                }
            }
        }
        return file.delete();
    }

    @Override
    public boolean updateEsRemoteExtDict(HttpServletRequest request, List<MultipartFile> attachments, String[] filesId) {
        SysUserDto userInfo = sysUserService.getUserByLoginToken(request);
        if (null != userInfo) {
            String filePath = env.getProperty("local.uploadPath") + "ESRemote";
            if (null != filesId && filesId.length > 0) {
                // 删除本地文件
                QueryWrapper<SysFileDto> query = new QueryWrapper<>();
                query.notIn("ID", filesId);
                List<SysFileDto> list = sysFileService.list(query);
                if (list.size() > 0) {
                    list.forEach(item -> {
                        this.deleteFolder(filePath + "/" + item.getFilename());
                    });
                }
                // 删除数据库中数据
                QueryWrapper<SysFileDto> projectFileQueryWrapper = new QueryWrapper<>();
                projectFileQueryWrapper.notIn("ID", filesId);
                sysFileService.remove(projectFileQueryWrapper);
            }
            // 添加附件
            if (attachments.size() > 0) {
                attachments.forEach(item -> {
                    // 获取指定文件夹
                    String dirUrl = "ESRemote/";
                    if ("remoteExtDic.txt".equals(item.getOriginalFilename()) || "remoteBanDic.txt".equals(item.getOriginalFilename())) {
                        try {
                            SysFileDto projectFile = this.saveFile(item, dirUrl, request);
                            projectFile.setType("ES");
                            projectFile.setHandlerId(userInfo.getId());
                            QueryWrapper<SysFileDto> projectFileQueryWrapper = new QueryWrapper<>();
                            projectFileQueryWrapper.eq("FILENAME", item.getOriginalFilename());
                            sysFileService.remove(projectFileQueryWrapper);
                            sysFileService.save(projectFile);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            throw new CustomException(ErrorEnum.E_422.getErrorCode(), "上传文件出错");
                        }
                    }
                });

            }
            // 全部删除
            if (null == filesId && attachments.size() == 0) {
                QueryWrapper<SysFileDto> projectFileQueryWrapper = new QueryWrapper<>();
                projectFileQueryWrapper.eq("is_del", 0);
                sysFileService.remove(projectFileQueryWrapper);

                this.deleteFolder(filePath);
            }
            return true;
        }
        throw new CustomException(ErrorEnum.E_422.getErrorCode(), "上传失败");
    }

    @Override
    public ResponseResultDto getEsRemoteDict() {
        QueryWrapper<SysFileDto> projectFileQueryWrapper = new QueryWrapper<>();
        projectFileQueryWrapper.eq("FILENAME", "remoteExtDic.txt")
                .or()
                .eq("FILENAME", "remoteBanDic.txt");
        List<SysFileDto> list = sysFileService.list(projectFileQueryWrapper);
        return ResponseResultDto.ok().data("data", list);
    }
}
