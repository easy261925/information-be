package com.th.workbase.service.system.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.th.workbase.bean.equipment.MessageDto;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysConfig;
import com.th.workbase.mapper.system.SysConfigMapper;
import com.th.workbase.service.common.JPushService;
import com.th.workbase.service.system.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author cc
 * @since 2021-02-25
 */
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    @Autowired
    JPushService jPushService;

    @Override
    public ResponseResultDto getSysConfig() {
        try {
            List<SysConfig> list = baseMapper.selectList(null);
            return ResponseResultDto.ok().data("data", list.get(0));
        } catch (Exception e) {
            return ResponseResultDto.error();
        }
    }

    @Override
    public ResponseResultDto updateSysConfig(HttpServletRequest request, SysConfig sysConfig) {
        List<SysConfig> list = baseMapper.selectList(null);
        if (list.size() > 0) {
            Integer id = list.get(0).getId();
            sysConfig.setId(id);
            baseMapper.updateById(sysConfig);
        } else {
            baseMapper.insert(sysConfig);
        }
        MessageDto msg = new MessageDto();
        msg.setContent("更新版本");
        msg.setTitle("2");//更新版本时,借用title字段传递2,前台收到此字段进行设备配置更新
        jPushService.sendMsgToAll(msg);
        return ResponseResultDto.ok();
    }


}
