package com.th.workbase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.th.workbase.bean.Files;
import com.th.workbase.bean.equipment.EquipmentBreakDownDto;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysFileDto;
import com.th.workbase.common.utils.FileUtil;
import com.th.workbase.config.UrlFilesToZip;
import com.th.workbase.mapper.FilesMapper;
import com.th.workbase.service.FilesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author cc
 * @since 2021-05-11
 */
@Service
public class FilesServiceImpl extends ServiceImpl<FilesMapper, Files> implements FilesService {

    @Resource
    private Environment env;

    @Resource
    private FilesMapper filesMapper;

    //上传文件保存到本地
    public SysFileDto saveFile(MultipartFile file, String dirName, String filename) throws UnsupportedEncodingException {

        // 获取本地文件路径
        String filePath = env.getProperty("local.uploadPath");
        // 生成指定文件夹
        String dirUrl = filePath + dirName;
        try {
            // 创建文件夹
            File f = new File(dirUrl);
            f.mkdirs();
            //将文件保存到static文件夹里
            file.transferTo(new File(dirUrl + "/" + filename));
        } catch (Exception e) {
            e.printStackTrace();
        }
        SysFileDto sysFileDto = new SysFileDto();
        sysFileDto.setUrl("http://" + env.getProperty("local.ip") + ":" + env.getProperty("server.port") + "/static/files/" + dirName + "/" + filename);
        sysFileDto.setFilePath(dirUrl + filename);
        sysFileDto.setFilename(filename);
        return sysFileDto;
    }

    @Override
    public ResponseResultDto create(HashMap<String, Object> map) {
        System.out.println(map);
        Files payload = new Files();
        try {
            String townId = (String) map.get("townId");
            payload.setTownId(townId);
            String villageName = (String) map.get("villageName");
            payload.setVillageName(villageName);
            String username = (String) map.get("username");
            payload.setUsername(username);
            String phone = (String) map.get("phone");
            payload.setPhone(phone);
            String dirName = townId + villageName + username + phone;
            payload.setDirName(dirName);
            System.out.println("文件夹" + dirName);
            List<MultipartFile> ida = (List<MultipartFile>) map.get("IDA");
            ida.forEach(item -> {
                try {
                    SysFileDto idaFiles = saveFile(item, dirName, "户主身份证正面.jpg");
                    System.out.println(idaFiles.getUrl());
                    payload.setIda(idaFiles.getUrl());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });

            List<MultipartFile> idb = (List<MultipartFile>) map.get("IDB");
            idb.forEach(item -> {
                try {
                    SysFileDto ibdFiles = saveFile(item, dirName, "户主身份证背面.jpg");
                    System.out.println(ibdFiles);
                    payload.setIdb(ibdFiles.getUrl());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });

            List<MultipartFile> hkb = (List<MultipartFile>) map.get("HKB");
            String hkbText = "";
            for (int i = 0; i < hkb.size(); i++) {
                try {
                    SysFileDto sysFileDto = saveFile(hkb.get(i), dirName, "户主户口本" + i + ".jpg");
                    if (i == 0) {
                        hkbText += sysFileDto.getUrl();
                    } else {
                        hkbText += "," + sysFileDto.getUrl();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            payload.setHkb(hkbText);

            List<MultipartFile> FWCQZ = (List<MultipartFile>) map.get("FWCQZ");
            String FWCQZText = "";
            for (int i = 0; i < FWCQZ.size(); i++) {
                try {
                    SysFileDto sysFileDto = saveFile(FWCQZ.get(i), dirName, "房屋产权证" + i + ".jpg");
                    if (i == 0) {
                        FWCQZText += sysFileDto.getUrl();
                    } else {
                        FWCQZText += "," + sysFileDto.getUrl();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            payload.setFwcqz(FWCQZText);


            List<MultipartFile> TDSYZ = (List<MultipartFile>) map.get("TDSYZ");
            String TDSYZText = "";
            for (int i = 0; i < TDSYZ.size(); i++) {
                try {
                    SysFileDto sysFileDto = saveFile(TDSYZ.get(i), dirName, "土地使用证" + i + ".jpg");
                    if (i == 0) {
                        TDSYZText += sysFileDto.getUrl();
                    } else {
                        TDSYZText += "," + sysFileDto.getUrl();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            payload.setTdsyz(TDSYZText);


            List<MultipartFile> QTQSZM = (List<MultipartFile>) map.get("QTQSZM");
            String QTQSZMText = "";
            for (int i = 0; i < QTQSZM.size(); i++) {
                try {
                    SysFileDto sysFileDto = saveFile(QTQSZM.get(i), dirName, "其他权属证明" + i + ".jpg");
                    if (i == 0) {
                        QTQSZMText += sysFileDto.getUrl();
                    } else {
                        QTQSZMText += "," + sysFileDto.getUrl();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            payload.setQtqszm(QTQSZMText);

            List<MultipartFile> QTCL = (List<MultipartFile>) map.get("QTCL");
            String QTCLText = "";
            for (int i = 0; i < QTCL.size(); i++) {
                try {
                    SysFileDto sysFileDto = saveFile(QTCL.get(i), dirName, "其他材料" + i + ".jpg");
                    if (i == 0) {
                        QTCLText += sysFileDto.getUrl();
                    } else {
                        QTCLText += "," + sysFileDto.getUrl();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            payload.setQtcl(QTCLText);

            String usernameA1 = (String) map.get("usernameA1");
            payload.setUsernameA1(usernameA1);

            String phoneA1 = (String) map.get("phoneA1");
            payload.setPhoneA1(phoneA1);

            String usernameA2 = (String) map.get("usernameA2");
            payload.setUsernameA2(usernameA2);

            String phoneA2 = (String) map.get("phoneA2");
            payload.setPhoneA2(phoneA2);

            List<MultipartFile> IDA1 = (List<MultipartFile>) map.get("IDA1");
            IDA1.forEach(item -> {
                try {
                    SysFileDto sysFileDto = saveFile(item, dirName, "房屋持有人1身份证正面.jpg");
                    payload.setIda1(sysFileDto.getUrl());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });

            List<MultipartFile> IDB1 = (List<MultipartFile>) map.get("IDB1");
            IDB1.forEach(item -> {
                try {
                    SysFileDto sysFileDto = saveFile(item, dirName, "房屋持有人1身份证背面.jpg");
                    payload.setIdb1(sysFileDto.getUrl());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });

            List<MultipartFile> IDA2 = (List<MultipartFile>) map.get("IDA2");
            IDA2.forEach(item -> {
                try {
                    SysFileDto sysFileDto = saveFile(item, dirName, "房屋持有人2身份证正面.jpg");
                    payload.setIda2(sysFileDto.getUrl());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });

            List<MultipartFile> IDB2 = (List<MultipartFile>) map.get("IDB2");
            IDB2.forEach(item -> {
                try {
                    SysFileDto sysFileDto = saveFile(item, dirName, "房屋持有人2身份证背面.jpg");
                    payload.setIdb2(sysFileDto.getUrl());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });

            List<MultipartFile> HKB1 = (List<MultipartFile>) map.get("HKB1");
            String HKB1Text = "";
            for (int i = 0; i < HKB1.size(); i++) {
                try {
                    SysFileDto sysFileDto = saveFile(HKB1.get(i), dirName, "其他户口本" + i + ".jpg");
                    if (i == 0) {
                        HKB1Text += sysFileDto.getUrl();
                    } else {
                        HKB1Text += "," + sysFileDto.getUrl();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            payload.setHkb1(HKB1Text);

            List<MultipartFile> FWCQLY = (List<MultipartFile>) map.get("FWCQLY");
            String FWCQLYText = "";
            for (int i = 0; i < FWCQLY.size(); i++) {
                try {
                    SysFileDto sysFileDto = saveFile(FWCQLY.get(i), dirName, "房屋产权来源" + i + ".jpg");
                    if (i == 0) {
                        FWCQLYText += sysFileDto.getUrl();
                    } else {
                        FWCQLYText += "," + sysFileDto.getUrl();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            payload.setFwcqly(FWCQLYText);

            filesMapper.insert(payload);
            return ResponseResultDto.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResultDto.ServiceError("上传失败");
        }
    }

    @Override
    public ResponseResultDto getData(Files file, int current, int pageSize) {
        QueryWrapper<Files> filesQueryWrapper = new QueryWrapper<>();
        Page<Files> page = new Page<>(current, pageSize);
//        filesQueryWrapper.orderByDesc("dt_update_date_time");
        if (file.getUsername() != null) {
            filesQueryWrapper.like("USERNAME", file.getUsername());
        }
        if (file.getTownId() != null) {
            filesQueryWrapper.eq("TOWN_ID", file.getTownId());
        }
        if (file.getVillageName() != null) {
            filesQueryWrapper.like("VILLAGE_NAME", file.getVillageName());
        }
        Page<Files> filesPage = filesMapper.selectPage(page, filesQueryWrapper);
        List<Files> records = filesPage.getRecords();
        long total = filesPage.getTotal();

        return ResponseResultDto.ok().data("data", records).data("total", total);
    }

    @Override
    public String getFilesDirName(Files files) {
        if (files == null) {
            return null;
        }
        if (files.getDirName() != null) {
            return files.getDirName();
        }
        if (files.getIda() != null) {
            String dirNamePath = files.getIda().split("files/")[1];
            String dirName = dirNamePath.split("/")[0];
            return dirName;
        }
        if (files.getIdb() != null) {
            String dirNamePath = files.getIdb().split("files/")[1];
            String dirName = dirNamePath.split("/")[0];
            return dirName;
        }
        if (files.getHkb() != null) {
            String split = files.getHkb().split(",")[0];
            String dirNamePath = split.split("files/")[1];
            String dirName = dirNamePath.split("/")[0];
            return dirName;
        }
        if (files.getFwcqz() != null) {
            String split = files.getFwcqz().split(",")[0];
            String dirNamePath = split.split("files/")[1];
            String dirName = dirNamePath.split("/")[0];
            return dirName;
        }
        if (files.getTdsyz() != null) {
            String split = files.getTdsyz().split(",")[0];
            String dirNamePath = split.split("files/")[1];
            String dirName = dirNamePath.split("/")[0];
            return dirName;
        }
        if (files.getQtqszm() != null) {
            String split = files.getQtqszm().split(",")[0];
            String dirNamePath = split.split("files/")[1];
            String dirName = dirNamePath.split("/")[0];
            return dirName;
        }
        if (files.getQtcl() != null) {
            String split = files.getQtcl().split(",")[0];
            String dirNamePath = split.split("files/")[1];
            String dirName = dirNamePath.split("/")[0];
            return dirName;
        }
        if (files.getIda1() != null) {
            String dirNamePath = files.getIda1().split("files/")[1];
            String dirName = dirNamePath.split("/")[0];
            return dirName;
        }
        if (files.getIdb1() != null) {
            String dirNamePath = files.getIdb1().split("files/")[1];
            String dirName = dirNamePath.split("/")[0];
            return dirName;
        }
        if (files.getIdb2() != null) {
            String dirNamePath = files.getIdb2().split("files/")[1];
            String dirName = dirNamePath.split("/")[0];
            return dirName;
        }
        if (files.getHkb1() != null) {
            String split = files.getHkb1().split(",")[0];
            String dirNamePath = split.split("files/")[1];
            String dirName = dirNamePath.split("/")[0];
            return dirName;
        }
        if (files.getFwcqly() != null) {
            String split = files.getFwcqly().split(",")[0];
            String dirNamePath = split.split("files/")[1];
            String dirName = dirNamePath.split("/")[0];
            return dirName;
        }
        return null;
    }

    @Override
    public ResponseResultDto deleteFiles(String id) {
        Files files = filesMapper.selectById(id);
        // 获取文件夹名称
        String currentDirName = getFilesDirName(files);
        // 获取本地文件路径
        String filePath = env.getProperty("local.uploadPath");
        FileUtil.deleteDirectory(filePath + currentDirName);
        FileUtil.deleteFile(filePath + currentDirName + ".zip");
        filesMapper.deleteById(id);
        return ResponseResultDto.ok();
    }

    @Override
    public ResponseResultDto deleteAllFiles(Files file) {
        List<String> fileIdList = file.getFileIdList();
        for (String id : fileIdList) {
            Files files = filesMapper.selectById(id);
            // 获取文件夹名称
            String currentDirName = getFilesDirName(files);
            // 获取本地文件路径
            String filePath = env.getProperty("local.uploadPath");
            FileUtil.deleteDirectory(filePath + currentDirName);
            FileUtil.deleteFile(filePath + currentDirName + ".zip");
            filesMapper.deleteById(id);
        }
        return ResponseResultDto.ok();
    }

    @Override
    public ResponseResultDto downloadFiles(String id) {
        Files files = filesMapper.selectById(id);
        // 获取文件夹名称
        String currentDirName = getFilesDirName(files);
        String zipName = currentDirName + ".zip";
        // 获取本地文件路径
        String filePath = env.getProperty("local.uploadPath");
        try {
            boolean zipResult = UrlFilesToZip.createCardImgZip(filePath + currentDirName, currentDirName, filePath);
            if (zipResult) {
                files.setZipName(zipName);
                filesMapper.updateById(files);
                String url = "http://" + env.getProperty("local.ip") + ":" + env.getProperty("server.port") + "/static/files/" + zipName;
                return ResponseResultDto.ok().data("url", url);
            } else {
                return ResponseResultDto.ServiceError("压缩文件出错");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResultDto.ServiceError("压缩文件出错");
        }

    }
}
