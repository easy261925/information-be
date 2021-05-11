package com.th.workbase.service.equipment.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.th.workbase.bean.equipment.MessageDto;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.mapper.equipment.MessageMapper;
import com.th.workbase.service.equipment.MessageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author tangj
 * @since 2021-03-03
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, MessageDto> implements MessageService {
    @Resource
    MessageMapper messageMapper;

    public void addMessage(MessageDto msg) {
        messageMapper.insert(msg);
    }

    //分页查询所有的消息数据,按时间倒序
    public ResponseResultDto getMessageByPage(MessageDto msg, int current, int pageSize) {
        Page<MessageDto> page = new Page<>(current, pageSize);
        QueryWrapper<MessageDto> msgWrapper = new QueryWrapper<>();
        msgWrapper.orderByDesc("DT_CREA_DATE_TIME");
        Page<MessageDto> messageDtoPage = messageMapper.selectPage(page, msgWrapper);
        return ResponseResultDto.ok().data("data", messageDtoPage.getRecords());
    }
}
