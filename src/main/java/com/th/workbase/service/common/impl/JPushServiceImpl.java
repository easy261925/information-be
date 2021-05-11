package com.th.workbase.service.common.impl;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.th.workbase.bean.equipment.MessageDto;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.mapper.equipment.MessageMapper;
import com.th.workbase.service.common.JPushService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Date 2021-04-02-12:24
 * @Author tangJ
 * @Description
 * @Version 1.0
 */
@Slf4j
@Service
public class JPushServiceImpl implements JPushService {
    @Resource
    MessageMapper messageMapper;

    private static final String MASTER_SECRET = "7e1dcdaae2285d6498ca5ff1";
    private static final String APP_KEY = "a98846c6da639a500f300dc9";
    public static final String TITLE = "您有新的任务请注意查看";

    @Override
    public ResponseResultDto sendMsg(MessageDto msg) {
        if (StringUtils.isBlank(msg.getTitle())){
            msg.setTitle(TITLE);
        }
        ClientConfig clientConfig = ClientConfig.getInstance();
        final JPushClient jpushClient = new JPushClient(MASTER_SECRET, APP_KEY, null, clientConfig);

        final PushPayload payload = buildPushObject_android_and_ios(msg);

        try {
            PushResult result = jpushClient.sendPush(payload);
            //将推送的信息写到数据库中
            messageMapper.insert(msg);
            log.info("Got result - " + result);
        } catch (APIConnectionException e) {
            log.error("Connection error. Should retry later. ", e);
            log.error("Sendno: " + payload.getSendno());
            return ResponseResultDto.error();
        } catch (APIRequestException e) {
            log.error("Error response from JPush server. Should review and fix it. ", e);
            log.info("HTTP Status: " + e.getStatus());
            log.info("Error Code: " + e.getErrorCode());
            log.info("Error Message: " + e.getErrorMessage());
            log.info("Msg ID: " + e.getMsgId());
            log.error("Sendno: " + payload.getSendno());
            int errorCode = e.getErrorCode();
            if (errorCode == 1011) {
                return ResponseResultDto.error().code(422).message("当前设备不在线，请稍后尝试");
            }
        }
        return ResponseResultDto.ok();
    }

    private PushPayload buildPushObject_android_and_ios(MessageDto msg) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.tag(msg.getReceiver()))//设备编码
                .setNotification(Notification.newBuilder()
                        .setAlert(msg.getContent())//信息内容
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setTitle(msg.getTitle())
                                .addExtra("equipmentType", msg.getEquipmentType())
                                .build())
                        .build())
                .build();
    }

    @Override
    public ResponseResultDto sendMsgToAll(MessageDto msg) {
        ClientConfig clientConfig = ClientConfig.getInstance();
        final JPushClient jpushClient = new JPushClient(MASTER_SECRET, APP_KEY, null, clientConfig);

        final PushPayload payload = buildPushObject_android_and_iosToAll(msg);

        try {
            PushResult result = jpushClient.sendPush(payload);
            //将推送的信息写到数据库中
            messageMapper.insert(msg);
            log.info("Got result - " + result);
        } catch (APIConnectionException e) {
            log.error("Connection error. Should retry later. ", e);
            log.error("Sendno: " + payload.getSendno());
            return ResponseResultDto.error();
        } catch (APIRequestException e) {
            log.error("Error response from JPush server. Should review and fix it. ", e);
            log.info("HTTP Status: " + e.getStatus());
            log.info("Error Code: " + e.getErrorCode());
            log.info("Error Message: " + e.getErrorMessage());
            log.info("Msg ID: " + e.getMsgId());
            log.error("Sendno: " + payload.getSendno());
            int errorCode = e.getErrorCode();
            if (errorCode == 1011) {
                return ResponseResultDto.error().code(422).message("当前设备不在线，请稍后尝试");
            }
        }
        return ResponseResultDto.ok();
    }

    private PushPayload buildPushObject_android_and_iosToAll(MessageDto msg) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.all())//设备编码
                .setNotification(Notification.newBuilder()
                        .setAlert("")//信息内容
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setTitle(msg.getTitle())
                                .build())
                        .build())
                .build();
    }
}
