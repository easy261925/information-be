package com.th.workbase.mapper.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.th.workbase.bean.system.VersionManager;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 *
 * @author tangj
 * @since 2021-03-11
 */
public interface VersionManagerMapper extends BaseMapper<VersionManager> {

    Page<VersionManager> getUploadRecords(Page<VersionManager> page);
}
