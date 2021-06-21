package com.th.workbase.service.baidu.impl;

import java.io.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.th.workbase.Exception.CustomException;
import com.th.workbase.bean.Files;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysFileDto;
import com.th.workbase.common.FileTransferUtil;
import com.th.workbase.common.utils.HttpUtil;
import com.th.workbase.config.UrlFilesToZip;
import com.th.workbase.mapper.FilesMapper;
import com.th.workbase.service.FilesService;
import com.th.workbase.service.baidu.BaiduService;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author cc
 * @date 2021-06-09-下午10:26
 */
@Service
public class BaiduServiceImpl implements BaiduService {
    @Resource
    private Environment env;

    @Resource
    private FilesMapper filesMapper;

    @Resource
    private FilesService filesService;

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
    public String getAccessToken() {
        Map<String, String> params = new HashMap<>();
        params.put("grant_type", "client_credentials");
        params.put("client_id", "eEbHBWYGPzsCgC3frGT8gkcs");
        params.put("client_secret", "KihyG7eGh0sy4Sm3EiP61q5GRmi2ZjfK");
        String result = HttpUtil.post("https://aip.baidubce.com/oauth/2.0/token", params);
        HashMap hashMap = JSON.parseObject(result, HashMap.class);
        Object access_token = hashMap.get("access_token");
        System.out.println("access_token" + access_token);
        return (String) access_token;
    }

    @Override
    public ResponseResultDto zipFilesById(String id) {
        Files files = filesMapper.selectById(id);
        // 获取本地文件路径
        String filePath = env.getProperty("local.uploadPath");
        String dirName = "";
        // 获取文件夹名称
        String currentDirName = filesService.getFilesDirName(files);
        if (currentDirName != null) {
            dirName = currentDirName;
            try {
                UrlFilesToZip.ZipImage(filePath + dirName, filePath + currentDirName);
                files.setZipImages("1");
                filesMapper.updateById(files);
                return ResponseResultDto.ok();
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseResultDto.ServiceError("压缩图片出错");
            }

        }
        return ResponseResultDto.ServiceError("没有文件可以压缩");
    }

    @Override
    public String imageToString(String imageUrl) {
        // 请求url
//        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/webimage";
        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/accurate_basic";
        try {
            String param = "url=" + imageUrl + "&detect_direction=true";
            String accessToken = getAccessToken();
            String result = com.th.workbase.common.HttpUtil.post(url, accessToken, param);
            System.out.println("转换结果为=" + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(422, "文字转换出错");
        }
    }

    public void setParagraph(XWPFDocument document, String text, int fontSize) {
        if (text == null) {
            return;
        }
        XWPFParagraph firstParagraph = document.createParagraph();
        XWPFRun run = firstParagraph.createRun();
        run.setText(text);
        run.setColor("000000");
        if (fontSize == 12) {
            run.setColor("333333");
        } else if (fontSize == 8) {
            run.setColor("4bba79");
        }
        run.setFontSize(fontSize);
    }

    public void setParagraphByImages(XWPFDocument document, String title, String dataSource) {
        if (dataSource == null) {
            return;
        }
        try {
            String[] data = dataSource.split((","));
            if (data.length > 0) {
                setParagraph(document, title, 12);
                for (int i = 0; i < data.length; i++) { // TODO
                    String url = data[i];
                    int index = i + 1;
                    setParagraph(document, title + index, 10);
                    String s = imageToString(url);
                    if (s != null) {
                        JSONObject jsonObject = JSONObject.parseObject(s);
                        List<Object> words_result = (List<Object>) jsonObject.get("words_result");
                        if (words_result != null && words_result.size() > 0) {
                            words_result.forEach(resultItem -> {
                                if (resultItem != null) {
                                    if (resultItem instanceof com.alibaba.fastjson.JSONObject) {
                                        Object words = ((JSONObject) resultItem).get("words");
                                        setParagraph(document, words.toString(), 8);
                                    }
                                }
                            });
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean makeWordFile(String id) {
        try {
            Files result = filesMapper.selectById(id);
            String townId = result.getTownId(); // 镇名
            String villageName = result.getVillageName(); // 村名
            String username = result.getUsername(); // 户主姓名
            String phone = result.getPhone(); // 户主电话
            String ida = result.getIda(); // 身份证正面
            String idb = result.getIdb(); // 身份证背面
            String hkb = result.getHkb(); // 户主户口本
            String fwcqz = result.getFwcqz(); // 房屋产权证
            String tdsyz = result.getTdsyz(); // 土地使用证
            String qtqszm = result.getQtqszm(); // 其他产权证明
            String qtcl = result.getQtcl(); // 其他材料
            String usernameA1 = result.getUsernameA1();
            String phoneA1 = result.getPhoneA1();
            String ida1 = result.getIda1();
            String ida2 = result.getIda2();
            String usernameA2 = result.getUsernameA2();
            String phoneA2 = result.getPhoneA2();
            String idb1 = result.getIdb1();
            String idb2 = result.getIdb2();
            String hkb1 = result.getHkb1();
            String fwcqly = result.getFwcqly();

            String title = townId + villageName + username;
            String dirName = "";
            // 获取文件夹名称
            String currentDirName = filesService.getFilesDirName(result);
            if (currentDirName != null) {
                dirName = currentDirName;
            } else {
                dirName = title + phone;
            }

            // 创建临时文件
            XWPFDocument document = new XWPFDocument();
            File currentFile = new File("temp.docx");
            FileOutputStream out = new FileOutputStream(currentFile);

            // 添加标题
            XWPFParagraph titleParagraph = document.createParagraph();
            // 设置段落居中
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleParagraphRun = titleParagraph.createRun();
            titleParagraphRun.setText(title);
            titleParagraphRun.setColor("000000");
            titleParagraphRun.setFontSize(20);
            System.out.println("dirName" + dirName);

            // 设置镇明
            setParagraph(document, "镇名:" + townId, 12);
            setParagraph(document, "村名:" + villageName, 12);
            setParagraph(document, "户主姓名:" + username, 12);
            setParagraph(document, "户主电话:" + phone, 12);
            setParagraphByImages(document, "户主身份证正面信息:", ida);
//            setParagraphByImages(document, "户主身份证背面信息:", idb);
//            setParagraphByImages(document, "户主户口本:", hkb);
//            setParagraphByImages(document, "房屋产权证:", fwcqz);
//            setParagraphByImages(document, "土地使用证:", tdsyz);
//            setParagraphByImages(document, "其他产权证明:", qtqszm);
//            setParagraphByImages(document, "其他材料:", qtcl);
            if (usernameA1 != null) {
                setParagraph(document, "房屋持有人（1）姓名:" + usernameA1, 12);
            }
            if (phoneA1 != null) {
                setParagraph(document, "房屋持有人（1）手机:" + phoneA1, 12);
            }
            setParagraphByImages(document, "房屋持有人（1）身份证正面:", ida1);
//            setParagraphByImages(document, "房屋持有人（1）身份证背面:", ida2);
            if (usernameA2 != null) {
                setParagraph(document, "房屋持有人（2）姓名:" + usernameA2, 12);
            }
            if (phoneA2 != null) {
                setParagraph(document, "房屋持有人（2）手机:" + phoneA2, 12);
            }
            setParagraphByImages(document, "房屋持有人（2）身份证正面:", idb1);
//            setParagraphByImages(document, "房屋持有人（2）身份证背面:", idb2);
//            setParagraphByImages(document, "房屋持有人户口本信息:", hkb1);
//            setParagraphByImages(document, "房屋产权来源:", fwcqly);

            document.write(out);
            out.close();
            System.out.println("create_table document written success.");
            SysFileDto sysFileDto = saveFile(FileTransferUtil.fileToMultipartFile(currentFile), dirName, title + ".doc");
            result.setTransfer("1");
            result.setWordUrl(sysFileDto.getUrl());
            filesMapper.updateById(result);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
