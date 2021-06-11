package com.th.workbase.controller.baidu;

import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.service.baidu.BaiduService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.FileNotFoundException;

/**
 * @author cc
 * @date 2021-06-09-下午10:14
 */
@RestController
@RequestMapping("/baidu")
public class baiduApi {

    @Resource
    BaiduService baiduService;

    @GetMapping("/getAccessToken")
    public ResponseResultDto getAccessToken() {
        baiduService.getAccessToken();
        return ResponseResultDto.ok();
    }


    @GetMapping("/transfer")
    public ResponseResultDto transfer() {
        String words = baiduService.imageToString("http://60.205.181.129:7003/static/files/平安镇哈吧村委会马晓平 汤凤云1622156262186/户主身份证正面.jpg");
        return ResponseResultDto.ok().data("words", words);
    }

    @GetMapping("/makeFile/{id}")
    public ResponseResultDto makeFile(@PathVariable("id") String id) {
        try {
            boolean success = baiduService.makeWordFile(id);
            if (success) {
                return ResponseResultDto.ok();
            }
            return ResponseResultDto.ServiceError("转换出错");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResultDto.ServiceError("转换出错");
        }
    }
}
