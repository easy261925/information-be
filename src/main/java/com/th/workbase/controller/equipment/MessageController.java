package com.th.workbase.controller.equipment;


import com.th.workbase.bean.equipment.MessageDto;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.service.equipment.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author tangj
 * @since 2021-03-03
 */
@RestController
@RequestMapping("/message")
@Api(tags = {"消息管理"})
public class MessageController {
    @Autowired
    MessageService messageService;
    @ApiOperation(value = "消息查询", notes = "消息分页查询")
    @GetMapping("/message")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "第几页", dataType = "int", required = true, example = "1"),
            @ApiImplicitParam(name = "pageSize", value = "一页显示多少记录", dataType = "int", required = true, example = "20")
    })
    public ResponseResultDto getMessageByPage(MessageDto msg, int current, int pageSize) {
        return messageService.getMessageByPage(msg, current, pageSize);
    }

}

