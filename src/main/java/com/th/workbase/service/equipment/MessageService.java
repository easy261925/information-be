package com.th.workbase.service.equipment;

import com.baomidou.mybatisplus.extension.service.IService;
import com.th.workbase.bean.equipment.MessageDto;
import com.th.workbase.bean.system.ResponseResultDto;

/**
 * @author tangj
 * @since 2021-03-03
 */
public interface MessageService extends IService<MessageDto> {
    ResponseResultDto getMessageByPage(MessageDto msg, int current, int pageSize);
}
