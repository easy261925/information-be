package com.th.workbase.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.VersionManager;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author tangj
 * @since 2021-03-11
 */
public interface VersionManagerService extends IService<VersionManager> {


    ResponseResultDto deleteVersionManager(String id);

    ResponseResultDto uploadApp(MultipartFile file, int forceUpdate, String appVersion, String remark);

    Object checkUpdate(String version);

    ResponseResultDto getUploadRecord(int current, int pageSize);
}
