package com.th.workbase.service.equipment.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.th.workbase.bean.equipment.CurrentPositionDto;
import com.th.workbase.bean.equipment.EquipmentHistoryDto;
import com.th.workbase.bean.equipment.MessageDto;
import com.th.workbase.bean.plan.PlanTaskDto;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysConfig;
import com.th.workbase.common.commonEnum.EquipmentType;
import com.th.workbase.common.commonEnum.TaskState;
import com.th.workbase.common.utils.CalculateDistanceUtil;
import com.th.workbase.mapper.equipment.CurrentPositionMapper;
import com.th.workbase.mapper.equipment.EquipmentHistoryMapper;
import com.th.workbase.mapper.plan.PlanTaskMapper;
import com.th.workbase.mapper.system.SysConfigMapper;
import com.th.workbase.service.common.JPushService;
import com.th.workbase.service.equipment.EquipmentHistoryService;
import com.th.workbase.service.plan.PlanTaskService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.springframework.beans.BeanUtils.copyProperties;

/**
 * @author cc
 * @since 2021-02-28
 */
@Service
public class EquipmentHistoryServiceImpl extends ServiceImpl<EquipmentHistoryMapper, EquipmentHistoryDto> implements EquipmentHistoryService {
    @Resource
    CurrentPositionMapper currentPositionMapper;
    @Resource
    EquipmentHistoryMapper equipmentHistoryMapper;
    @Resource
    PlanTaskMapper taskMapper;
    @Resource
    SysConfigMapper configMapper;
    @Autowired
    JPushService jPushService;
    @Autowired
    PlanTaskService planTaskService;

    @Override
    public ResponseResultDto createEquipmentHistory(HttpServletRequest request, EquipmentHistoryDto equipmentHistoryDto) {
        //????????????????????????????????????
        QueryWrapper<CurrentPositionDto> currentWrapper = new QueryWrapper<>();
        currentWrapper.eq("EQUIPMENT_NO", equipmentHistoryDto.getEquipmentNo());
        CurrentPositionDto currentPosition = currentPositionMapper.selectOne(currentWrapper);
        if (currentPosition != null) {
            //??????????????????????????????,???????????????,??????????????????????????????
            Integer id = currentPosition.getId();
            copyProperties(equipmentHistoryDto, currentPosition);
            currentPosition.setId(id);
            currentPositionMapper.updateById(currentPosition);
        } else {
            CurrentPositionDto currentPositionDto = new CurrentPositionDto();
            copyProperties(equipmentHistoryDto, currentPositionDto);
            currentPositionMapper.insert(currentPositionDto);
        }
        baseMapper.insert(equipmentHistoryDto);
        String taskId = equipmentHistoryDto.getTaskId();
        //?????????????????????,?????????????????????????????????????????????
        if (StringUtils.isNotBlank(taskId)) {
            PlanTaskDto taskDto = taskMapper.selectById(taskId);
            String taskState = taskDto.getTaskState();
            if (taskState.compareTo(TaskState.LeaveShovel.getState()) < 0) {
                return ResponseResultDto.ok();
            }
            SysConfig sysConfig = configMapper.selectOne(null);
            String ncLnglat = sysConfig.getNcLnglat();
            String[] splitNc = ncLnglat.split("_");
            double ncLng = Double.parseDouble(splitNc[0]);//????????????
            double ncLat = Double.parseDouble(splitNc[1]);//????????????
            Integer ncRadius = sysConfig.getNcRadius();
            String bcLnglat = sysConfig.getBcLnglat();
            String[] splitBc = bcLnglat.split("_");
            double bcLng = Double.parseDouble(splitBc[0]);//????????????
            double bcLat = Double.parseDouble(splitBc[1]);//????????????
            Integer bcRadius = sysConfig.getBcRadius();

            //??????????????????????????????????????????
            Date now = new Date();
            Date now_1 = new Date(now.getTime() - 60000); //1??????????????????
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String previousMinute = dateFormat.format(now_1);
            List<EquipmentHistoryDto> equipmentHistoryDtos = equipmentHistoryMapper.selectPositionInLastMinute(equipmentHistoryDto.getEquipmentNo(), previousMinute);
            int count = 0;//???????????????????????????
            for (EquipmentHistoryDto historyDto : equipmentHistoryDtos) {
                Double currentLat = historyDto.getLat();
                Double currentLng = historyDto.getLng();
                double distance;
                if ("BC_PY".equals(taskDto.getDestination())) {
                    distance = CalculateDistanceUtil.getDistance(currentLat, currentLng, bcLat, bcLng);
                    if (distance < bcRadius) {
                        count++;
                    }
                } else if ("NC_PY".equals(taskDto.getDestination())) {
                    distance = CalculateDistanceUtil.getDistance(currentLat, currentLng, ncLat, ncLng);
                    if (distance < ncRadius) {
                        count++;
                    }
                }
                if (count == 1) {
                    //??????????????????,????????????
                    MessageDto msg = new MessageDto();
                    msg.setSender("system");
                    msg.setReceiver(equipmentHistoryDto.getEquipmentNo());
                    msg.setContent("????????????????????????,????????????????????????????????????");
                    msg.setEquipmentType(EquipmentType.Cart.getType());
                    msg.setFkTaskId(0);
                    jPushService.sendMsg(msg);
                    taskDto.setTaskState(TaskState.LeaveField.getState());
                    taskMapper.updateById(taskDto);
                    planTaskService.getNextTask(equipmentHistoryDto.getEquipmentNo());
                    return ResponseResultDto.ok().data("clearPyTaskId", true);
                }
            }
        }
        return ResponseResultDto.ok().data("clearPyTaskId", false);
    }

    @Override
    public ResponseResultDto getEquipmentHistory(HttpServletRequest request, EquipmentHistoryDto equipmentHistoryDto) {
        List<EquipmentHistoryDto> data = equipmentHistoryMapper.getHistoryPosition(equipmentHistoryDto);
        return ResponseResultDto.ok().data("data", data);
    }
}
