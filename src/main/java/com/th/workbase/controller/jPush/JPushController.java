package com.th.workbase.controller.jPush;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.th.workbase.bean.system.ResponseResultDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import cn.jpush.api.JPushClient;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author cc
 * @date 2021-01-13-上午10:18
 */
@RestController
@RequestMapping("/jPush")
@Api(tags = "jPush 测试")
@Slf4j
public class JPushController {

    protected static final String MASTER_SECRET = "7e1dcdaae2285d6498ca5ff1";
    protected static final String APP_KEY = "a98846c6da639a500f300dc9";

    public static final String TITLE = "您有新的任务请注意查看";
    public static final String ALERT = "Test from API Example - alert";
    public static final String MSG_CONTENT = "请前往3#电铲装岩石，之后前往1#破碎站";
    public static final String TAG = "tag_api";


    @PostMapping("/send")
    @ApiOperation("发送信息")
    public ResponseResultDto send() {
        ClientConfig clientConfig = ClientConfig.getInstance();
        final JPushClient jpushClient = new JPushClient(MASTER_SECRET, APP_KEY, null, clientConfig);

        final PushPayload payload = buildPushObject_android_and_ios();

        try {
            PushResult result = jpushClient.sendPush(payload);
            log.info("Got result - " + result);
            System.out.println(result);
            // 如果使用 NettyHttpClient，需要手动调用 close 方法退出进程
            // If uses NettyHttpClient, call close when finished sending request, otherwise process will not exit.
            // jpushClient.close();
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

    public static PushPayload buildPushObject_android_and_ios() {
        Map<String, String> extras = new HashMap<String, String>();
//        extras.put("test", "https://community.jiguang.cn/push");
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
//                .setPlatform(Platform.android())
                .setAudience(Audience.alias("test1"))//设备编码
//                .setAudience(Audience.newBuilder()
//                        .addAudienceTarget(AudienceTarget.tag("CARTS"))
//                        .build())
                .setMessage(Message.content("123456"))
                .setNotification(Notification.newBuilder()
                        .setAlert(MSG_CONTENT)//信息内容
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setTitle(TITLE)
                                .addExtra("sId", "3#电铲")
                                .addExtra("pId", "1#破碎")
                                .addExtras(extras).build())
                        .build())
                .build();
    }
}
