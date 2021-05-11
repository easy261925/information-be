package com.th.workbase.controller.system;


import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.service.system.VersionManagerService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author tangj
 * @since 2021-03-11
 */
@RestController
@RequestMapping("/versionManager")
public class VersionManagerController {
    @Autowired
    VersionManagerService versionManagerService;


    @ApiOperation(value = "上传app文件", notes = "上传app文件")
    @PostMapping("/upload")
    public ResponseResultDto uploadApp(@RequestPart MultipartFile file,
                                       @RequestParam("forceUpdate") int forceUpdate,
                                       @RequestParam("appVersion") String appVersion,
                                       @RequestParam("remark") String remark) {

        return versionManagerService.uploadApp(file, forceUpdate, appVersion, remark);
    }

    @ApiOperation(value = "查询上传记录", notes = "查询上传记录")
    @GetMapping("/upload")
    public ResponseResultDto getUploadRecord(int current, int pageSize) {

        return versionManagerService.getUploadRecord(current, pageSize);
    }

    @ApiOperation(value = "测速", notes = "测速")
    @PostMapping("/checkSpeed")
    public ResponseResultDto checkSpeed() {
        return ResponseResultDto.ok();
    }

    @ApiOperation(value = "检查升级信息", notes = "检查升级信息")
    @GetMapping("/checkUpdate")
    public Object checkUpdate(String versionCode) {
        return versionManagerService.checkUpdate(versionCode);
    }

    @DeleteMapping("/upload/{id}")
    public ResponseResultDto deleteVersionManager(@PathVariable("id") String id) {
        return versionManagerService.deleteVersionManager(id);
    }
}

