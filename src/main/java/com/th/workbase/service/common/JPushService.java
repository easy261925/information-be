package com.th.workbase.service.common;

import com.th.workbase.bean.equipment.MessageDto;
import com.th.workbase.bean.system.ResponseResultDto;

/**
 * @Date 2021-04-02-12:23
 * @Author tangJ
 * @Description 推送消息服务
 * @Version 1.0
 */
public interface JPushService {
    ResponseResultDto sendMsgToAll(MessageDto msg);

    ResponseResultDto sendMsg(MessageDto msg);
}
