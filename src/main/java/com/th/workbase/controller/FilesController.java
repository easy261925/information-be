package com.th.workbase.controller;


import com.th.workbase.bean.Files;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysLogDto;
import com.th.workbase.service.FilesService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author cc
 * @since 2021-05-11
 */
@RestController
@RequestMapping("/files")
public class FilesController {

    @Resource
    private FilesService filesService;

    @PostMapping("/create")
    @ApiOperation("上传文件")
    public ResponseResultDto create(HttpServletRequest request) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        HashMap<String, Object> map = new HashMap<>();

        String[] townId = request.getParameterValues("townId");
        String[] villageName = request.getParameterValues("villageName");
        String[] username = request.getParameterValues("username");
        String[] phone = request.getParameterValues("phone");
        List<MultipartFile> IDA = multipartRequest.getFiles("IDA");
        List<MultipartFile> IDB = multipartRequest.getFiles("IDB");
        List<MultipartFile> HKB = multipartRequest.getFiles("HKB");
        List<MultipartFile> FWCQZ = multipartRequest.getFiles("FWCQZ");
        List<MultipartFile> TDSYZ = multipartRequest.getFiles("TDSYZ");
        List<MultipartFile> QTQSZM = multipartRequest.getFiles("QTQSZM");
        List<MultipartFile> QTCL = multipartRequest.getFiles("QTCL");
        String[] usernameA1 = request.getParameterValues("usernameA1");
        String[] phoneA1 = request.getParameterValues("phoneA1");
        String[] usernameA2 = request.getParameterValues("usernameA2");
        String[] phoneA2 = request.getParameterValues("phoneA2");
        List<MultipartFile> IDA1 = multipartRequest.getFiles("IDA1");
        List<MultipartFile> IDB1 = multipartRequest.getFiles("IDB1");
        List<MultipartFile> IDA2 = multipartRequest.getFiles("IDA2");
        List<MultipartFile> IDB2 = multipartRequest.getFiles("IDB2");
        List<MultipartFile> HKB1 = multipartRequest.getFiles("HKB1");
        List<MultipartFile> FWCQLY = multipartRequest.getFiles("FWCQLY");
        map.put("townId", townId.length > 0 ? townId[0] : null);
        map.put("villageName", villageName.length > 0 ? villageName[0] : null);
        map.put("username", username.length > 0 ? username[0] : null);
        map.put("phone", phone.length > 0 ? phone[0] : null);
        map.put("IDA", IDA);
        map.put("IDB", IDB);
        map.put("HKB", HKB);
        map.put("FWCQZ", FWCQZ);
        map.put("TDSYZ", TDSYZ);
        map.put("QTQSZM", QTQSZM);
        map.put("QTCL", QTCL);
        map.put("usernameA1", usernameA1.length > 0 ? usernameA1[0] : null);
        map.put("phoneA1", phoneA1.length > 0 ? phoneA1[0] : null);
        map.put("usernameA2", usernameA2.length > 0 ? usernameA2[0] : null);
        map.put("phoneA2", phoneA2.length > 0 ? phoneA2[0] : null);
        map.put("IDA1", IDA1);
        map.put("IDB1", IDB1);
        map.put("IDA2", IDA2);
        map.put("IDB2", IDB2);
        map.put("HKB1", HKB1);
        map.put("FWCQLY", FWCQLY);

        return filesService.create(map);
    }


    @GetMapping(value = "/files")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "第几页", dataType = "int", required = true, example = "1"),
            @ApiImplicitParam(name = "pageSize", value = "一页显示多少记录", dataType = "int", required = true, example = "20"),
    })
    public ResponseResultDto getFiles(int current, int pageSize, Files file, HttpServletResponse response) {
        return filesService.getData(file, current, pageSize);
    }
}

