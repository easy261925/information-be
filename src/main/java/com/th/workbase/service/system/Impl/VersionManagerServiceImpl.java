package com.th.workbase.service.system.Impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.th.workbase.bean.equipment.MessageDto;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.VersionManager;
import com.th.workbase.mapper.system.VersionManagerMapper;
import com.th.workbase.service.common.JPushService;
import com.th.workbase.service.system.VersionManagerService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tangj
 * @since 2021-03-11
 */
@Service
public class VersionManagerServiceImpl extends ServiceImpl<VersionManagerMapper, VersionManager> implements VersionManagerService {
    @Resource
    VersionManagerMapper versionManagerMapper;
    @Resource
    private Environment env;
    @Value("${serverAddress}")
    private String SERVER_ADDRESS;
    @Autowired
    JPushService jPushService;


    @Override
    public ResponseResultDto deleteVersionManager(String id) {
        versionManagerMapper.deleteById(id);
        return ResponseResultDto.ok();
    }

    @Override
    public ResponseResultDto uploadApp(MultipartFile file, int forceUpdate, String appVersion, String remark) {
        VersionManager versionManager = new VersionManager();
        String path = env.getProperty("local.uploadPath");
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        try {
            String md5 = DigestUtils.md5Hex(file.getBytes());
            versionManager.setApkMd5(md5);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] appVersionSplit = appVersion.split("\\+");
        if (appVersionSplit.length == 1) {
            return ResponseResultDto.ServiceError("????????????????????????????????????????????????");
        }
        String filename = file.getOriginalFilename();
        String[] split = filename.split("\\.");
        if (split.length != 2) {
            return ResponseResultDto.ServiceError("???????????????????????????????????????");
        }
        //????????????????????????
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
        Date date = new Date();
        String format = simpleDateFormat.format(date);
        filename = split[0] + "_" + appVersionSplit[0] + "_" + format + "." + split[1];
        path = path + filename;
        versionManager.setAppVersion(appVersion);//???????????????
        versionManager.setRemark(remark);//????????????
        versionManager.setFileLocation(path);//??????????????????
        versionManager.setApkSize(file.getSize());//????????????
        versionManager.setOriginalFileName(filename);//????????????

        versionManagerMapper.insert(versionManager);
        try {
            file.transferTo(new File(path));
            MessageDto msg = new MessageDto();
            msg.setTitle("1");//???????????????,??????title????????????1,???????????????????????????????????????
            msg.setContent("????????????");
            jPushService.sendMsgToAll(msg);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResultDto.ServiceError("??????????????????,???????????????");
        }
        return ResponseResultDto.ok();
    }

    @Override
    public Object checkUpdate(String versionCode) {
        Map<String, Object> map = new HashMap<>();
        QueryWrapper<VersionManager> versionWrapper = new QueryWrapper<>();
        versionWrapper.orderByDesc("DT_CREA_DATE_TIME");
        List<VersionManager> versionManagerList = versionManagerMapper.selectList(versionWrapper);
        if (versionManagerList != null && versionManagerList.size() > 0) {
            VersionManager versionManager = versionManagerList.get(0);
            String appVersion = versionManager.getAppVersion();
            String[] newestVersion = appVersion.split("\\+");
            //??????????????????versionCode????????????????????????versionCode,???????????????????????????
            int versionCodeInt = Integer.parseInt(versionCode);
            int newestVersionInt = Integer.parseInt(newestVersion[1]);
            if (versionCodeInt - newestVersionInt >= 0) {
                map.put("Code", 0);
                map.put("Msg", "");
                map.put("UpdateStatus", 0);
                return JSONObject.toJSON(map);
            }
            Integer forceUpdate = versionManager.getForceUpdate();
            Date createTime = versionManager.getDtCreaDateTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = sdf.format(createTime);
            String remark = versionManager.getRemark();
            map.put("Code", 0);
            map.put("Msg", "");
            if (forceUpdate == 1) {
                map.put("UpdateStatus", 2);
            } else {
                map.put("UpdateStatus", 1);
            }
            map.put("VersionCode", Integer.parseInt(newestVersion[1]));
            map.put("VersionName", newestVersion[0]);
            map.put("UploadTime", format);
            map.put("ModifyContent", remark);
            String downloadUrl = "http://" + SERVER_ADDRESS + "/static/files/" + versionManager.getOriginalFileName();
            map.put("DownloadUrl", downloadUrl);
            map.put("ApkSize", versionManager.getApkSize() / 1024);
            map.put("ApkMd5", versionManager.getApkMd5());
            return JSONObject.toJSON(map);
        }
        map.put("Code", 1);
        return JSONObject.toJSON(map);
    }

    @Override
    public ResponseResultDto getUploadRecord(int current, int pageSize) {
        //???????????????????????????
        Page<VersionManager> page = new Page<>(current, pageSize);
        Page<VersionManager> versionManagerPage = versionManagerMapper.getUploadRecords(page);
        List<VersionManager> versionManagerList = versionManagerPage.getRecords();
        for (VersionManager versionManager : versionManagerList) {
            String downloadUrl = "http://" + SERVER_ADDRESS + "/static/files/" + versionManager.getOriginalFileName();
            versionManager.setDownloadUrl(downloadUrl);
        }
        return ResponseResultDto.ok().data("data", versionManagerList).data("total", versionManagerPage.getTotal());
    }
}
