package com.th.workbase.service.system.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.th.workbase.bean.plan.PlanTaskDto;
import com.th.workbase.bean.system.LoadDistanceDto;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.mapper.plan.PlanTaskMapper;
import com.th.workbase.mapper.system.LoadDistanceMapper;
import com.th.workbase.service.equipment.EquipmentMainService;
import com.th.workbase.service.system.LoadDistanceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author tangj
 * @since 2021-03-30
 */
@Service
public class LoadDistanceServiceImpl extends ServiceImpl<LoadDistanceMapper, LoadDistanceDto> implements LoadDistanceService {

    @Resource
    LoadDistanceMapper loadDistanceMapper;
    @Autowired
    EquipmentMainService equipmentMainService;
    @Resource
    PlanTaskMapper taskMapper;

    @Override
    public ResponseResultDto getLoadDistance(LoadDistanceDto loadDistanceParam) {
        QueryWrapper<LoadDistanceDto> distanceWrapper = new QueryWrapper<>();
        distanceWrapper.eq("SCHEDULE_DATE",loadDistanceParam.getScheduleDate());
        distanceWrapper.eq("SHIFT_TYPE",loadDistanceParam.getShiftType());
        distanceWrapper.orderByAsc("FIELD_NO","SHOVEL_NO");
        List<LoadDistanceDto> loadDistanceDtoList = loadDistanceMapper.selectList(distanceWrapper);
        for (LoadDistanceDto loadDistanceDto : loadDistanceDtoList) {
            String shovelNo = loadDistanceDto.getShovelNo();
            String fieldNo = loadDistanceDto.getFieldNo();
            String shovelName = equipmentMainService.translateFromEquipNo2Name(shovelNo);
            String fieldName = equipmentMainService.translateFromEquipNo2Name(fieldNo);
            loadDistanceDto.setFieldName(fieldName);
            loadDistanceDto.setShovelName(shovelName);
        }
        return ResponseResultDto.ok().data("data", loadDistanceDtoList);
    }

    @Override
    public ResponseResultDto updateLoadDistance(List<LoadDistanceDto> loadDistanceList) {
        boolean duplicate = false;
        for (LoadDistanceDto loadDistanceDto : loadDistanceList) {
            Integer id = loadDistanceDto.getId();
            String shiftType = loadDistanceDto.getShiftType();
            String scheduleDate = loadDistanceDto.getScheduleDate();
            String shovelNo = loadDistanceDto.getShovelNo();
            String fieldNo = loadDistanceDto.getFieldNo();
            if (id != null) {
                //对内容进行更新
                loadDistanceMapper.updateById(loadDistanceDto);
            } else {
                //对内容进行新增
                //检查出发点和目的地是否已存在
                QueryWrapper<LoadDistanceDto> distanceWrapper = new QueryWrapper<>();
                distanceWrapper.eq("SHOVEL_NO", shovelNo);
                distanceWrapper.eq("FIELD_NO", fieldNo);
                distanceWrapper.eq("SHIFT_TYPE",shiftType);
                distanceWrapper.eq("SCHEDULE_DATE",scheduleDate);
                LoadDistanceDto distanceDto = loadDistanceMapper.selectOne(distanceWrapper);
                if (distanceDto != null) {
                    duplicate=true;
                    continue;
                }
                loadDistanceMapper.insert(loadDistanceDto);
            }
            QueryWrapper<PlanTaskDto> taskWrapper = new QueryWrapper<>();
            taskWrapper.eq("PUBLISHER",shovelNo);
            taskWrapper.eq("DESTINATION",fieldNo);
            taskWrapper.eq("SCHEDULE_DATE",scheduleDate);
            taskWrapper.eq("SHIFT_TYPE",shiftType);
            PlanTaskDto task = new PlanTaskDto();
            task.setDistance(loadDistanceDto.getDistance());
            taskMapper.update(task,taskWrapper);
        }
        if (duplicate){
            return ResponseResultDto.ServiceError("部分数据已存在,可以直接修改运输距离,不需要新建数据,请检查更新内容");
        }
        return ResponseResultDto.ok();
    }

    @Override
    public ResponseResultDto deleteLoadDistance(String id) {
        loadDistanceMapper.deleteById(id);
        return ResponseResultDto.ok();
    }

}
