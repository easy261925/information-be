package com.th.workbase.service.system.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysDictDto;
import com.th.workbase.common.utils.StringUtil;
import com.th.workbase.mapper.system.SysDictMapper;
import com.th.workbase.service.system.SysDictService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDictDto> implements SysDictService {
    @Resource
    private SysDictMapper sysDictMapper;

    @Override
    public ResponseResultDto addSysDict(SysDictDto sysDictDto) {
        QueryWrapper<SysDictDto> sysDictDtoQueryWrapper = new QueryWrapper<>();
        if (sysDictDto.getGroupNo() != null) {
            sysDictDtoQueryWrapper.eq("group_no", sysDictDto.getGroupNo());
            if (baseMapper.selectList(sysDictDtoQueryWrapper).size() > 0) {
                return ResponseResultDto.ServiceError("该字典编号已存在");
            }
        }
        List<SysDictDto> dictDtos = sysDictDto.getSysDictDto();
        if (dictDtos != null && dictDtos.size() > 0) {
            for (SysDictDto arg : dictDtos) {
                arg.setGroupNo(sysDictDto.getGroupNo());
                arg.setGroupName(sysDictDto.getGroupName());
                arg.setDictType("1");
                sysDictMapper.insert(arg);
            }
        }
        sysDictDto.setDictType("0");
        sysDictMapper.insert(sysDictDto);
        return ResponseResultDto.ok();
    }

    @Override
    public ResponseResultDto updateSysDict(SysDictDto sysDictDto) {
        SysDictDto resultDto = sysDictMapper.selectById(sysDictDto.getId());
        if (resultDto != null) {
            QueryWrapper<SysDictDto> deleteWrapper = new QueryWrapper<>();
            deleteWrapper.eq("group_no", resultDto.getGroupNo());
            deleteWrapper.eq("dict_type", "1");
            sysDictMapper.delete(deleteWrapper);
            List<SysDictDto> dictDtos = sysDictDto.getSysDictDto();
            if (dictDtos != null && dictDtos.size() > 0) {
                for (SysDictDto arg : dictDtos) {
                    arg.setGroupNo(resultDto.getGroupNo());
                    arg.setGroupName(resultDto.getGroupName());
                    arg.setDictType("1");
                    sysDictMapper.insert(arg);
                }
            }
            sysDictMapper.updateById(sysDictDto);
            return ResponseResultDto.ok();
        } else {
            return ResponseResultDto.ServiceError("录入信息不可为空");
        }
    }

    @Override
    public ResponseResultDto getSysDictByPage(SysDictDto sysDictDto, int current, int pageSize) {
        QueryWrapper<SysDictDto> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dict_type", "0");
        if (StringUtil.isNotNullOrEmpty(sysDictDto.getGroupNo())) {
            queryWrapper.eq("group_no", sysDictDto.getGroupNo());
        }
        if (StringUtil.isNotNullOrEmpty(sysDictDto.getGroupName())) {
            queryWrapper.like("group_name", sysDictDto.getGroupName());
        }
        if (sysDictDto.getIsUse() != null) {
            queryWrapper.eq("is_use", 1);
        }
        queryWrapper.orderByDesc("dt_crea_date_time");
        Page<SysDictDto> page = new Page<>(current, pageSize);
        IPage<SysDictDto> results = sysDictMapper.selectPage(page, queryWrapper);
        List<SysDictDto> records = results.getRecords();
        if (records != null && records.size() > 0) {
            for (SysDictDto dictDto : records) {
                QueryWrapper<SysDictDto> detialWrapper = new QueryWrapper<>();
                detialWrapper.eq("group_no", dictDto.getGroupNo());
                detialWrapper.eq("dict_type", "1");
                List<SysDictDto> detialList = sysDictMapper.selectList(detialWrapper);
                dictDto.setSysDictDto(detialList);
            }
        }
        return ResponseResultDto.ok().data("data", results.getRecords()).data("total", results.getTotal());
    }

    @Override
    public ResponseResultDto deleteSysDict(Integer id) {
        if (id != null) {
            SysDictDto dictDto = sysDictMapper.selectById(id);
            if (dictDto != null) {
                QueryWrapper<SysDictDto> deleteWrapper = new QueryWrapper<>();
                deleteWrapper.eq("group_no", dictDto.getGroupNo());
                sysDictMapper.delete(deleteWrapper);
            }
            return ResponseResultDto.ok();
        } else {
            return ResponseResultDto.ServiceError("字典ID不可为空");
        }
    }

    @Override
    public ResponseResultDto deleteSysDictByTypeId(Integer id) {
        if (id != null) {
            sysDictMapper.deleteById(id);
            return ResponseResultDto.ok();
        } else {
            return ResponseResultDto.ServiceError("字典分组ID不可为空");
        }
    }

    /**
     * @param groupNo    字典分组编码
     * @param dictNo     要查的字典编码
     * @return 字典内容
     */
    @Override
    public String queryDict(String groupNo, String dictNo) {
        try {
            QueryWrapper<SysDictDto> dictWrapper = new QueryWrapper<>();
            dictWrapper.eq("GROUP_NO", groupNo);
            dictWrapper.eq("DICT_TYPE", 1);
            dictWrapper.eq("DICT_NO", dictNo);
            return sysDictMapper.selectOne(dictWrapper).getDictName();
        } catch (Exception e) {
            e.printStackTrace();
            return "notFound";
        }
    }
}
