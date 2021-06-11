package com.th.workbase.service.baidu;

import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;

/**
 * @author cc
 * @date 2021-06-09-下午10:27
 */
public interface BaiduService {
    // 获取 access_token
    String getAccessToken();

    // 图像转文字
    String imageToString(String imageUrl);

    // 生成word
    boolean makeWordFile(String id) throws Exception;
}
