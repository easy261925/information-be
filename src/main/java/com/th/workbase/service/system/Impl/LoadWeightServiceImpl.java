package com.th.workbase.service.system.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.th.workbase.bean.system.LoadWeightDto;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.mapper.system.LoadWeightMapper;
import com.th.workbase.service.system.LoadWeightService;
import com.th.workbase.service.system.SysDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author tangj
 * @since 2021-03-31
 */
@Service
public class LoadWeightServiceImpl extends ServiceImpl<LoadWeightMapper, LoadWeightDto> implements LoadWeightService {
    @Resource
    LoadWeightMapper loadWeightMapper;
    @Autowired
    SysDictService sysDictService;

    @Override
    public ResponseResultDto getLoadWeight() {
        QueryWrapper<LoadWeightDto> weightWrapper = new QueryWrapper<>();
        weightWrapper.orderByAsc("CART_TYPE");
        List<LoadWeightDto> loadWeightList = loadWeightMapper.selectList(weightWrapper);
        for (LoadWeightDto weightDto : loadWeightList) {
            String cartType = weightDto.getCartType();
            String cartsTypeName = sysDictService.queryDict("carts_type", cartType);
            weightDto.setCartTypeName(cartsTypeName);
        }
        return ResponseResultDto.ok().data("data", loadWeightList).data("total", loadWeightList.size());
    }

    @Override
    public ResponseResultDto deleteLoadWeight(String id) {
        loadWeightMapper.deleteById(id);
        return ResponseResultDto.ok();
    }

    @Override
    public ResponseResultDto updateLoadWeight(List<LoadWeightDto> loadWeightList) {
        for (LoadWeightDto loadWeightDto : loadWeightList) {
            Integer id = loadWeightDto.getId();
            if (id == null) {
                String cartType = loadWeightDto.getCartType();
                QueryWrapper<LoadWeightDto> weightWrapper = new QueryWrapper<>();
                weightWrapper.eq("CART_TYPE", cartType);
                LoadWeightDto weightDto = loadWeightMapper.selectOne(weightWrapper);
                if (weightDto == null) {
                    loadWeightMapper.insert(loadWeightDto);
                }
            } else {
                loadWeightMapper.updateById(loadWeightDto);
            }
        }
        return ResponseResultDto.ok();
    }
}
