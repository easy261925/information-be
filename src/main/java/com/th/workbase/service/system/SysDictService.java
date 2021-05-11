package com.th.workbase.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysDictDto;

/**
 *
 * @author hut
 * @since 2020-09-29
 */
public interface SysDictService extends IService<SysDictDto> {
    ResponseResultDto addSysDict(SysDictDto sysDictDto);

    ResponseResultDto updateSysDict(SysDictDto sysDictDto);

    ResponseResultDto deleteSysDict(Integer id);

    ResponseResultDto getSysDictByPage(SysDictDto sysDictDto, int current, int pageSize);

    ResponseResultDto deleteSysDictByTypeId(Integer id);

    String queryDict(String groupNo, String dictNo);
}
