package com.bonc.assetservice.assetsync.testproducer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonc.assetservice.assetsync.model.AssetDataMsgVO;
import com.bonc.assetservice.assetsync.model.AssetsMsgVO;
import com.bonc.assetservice.assetsync.model.DimInfoMsgVO;
import com.bonc.assetservice.assetsync.model.GroupInfoMsgVO;
import com.bonc.framework.common.pojo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@Api(tags = "标签服务api发布于 - 向 mq 推送消息")
@RestController
@RequestMapping("/labelmqsend/v1")
public class LabelMQAddController {

    @Autowired
    private DefaultMQProducer defaultMQProducer;

    @ApiOperation( "表资产上下线推送信息")
    @GetMapping("/tableOnlie")
    public Object tableOnlie(AssetsMsgVO assetsVO) {
        Message msg = new Message();
        msg.setTopic("adb-unicom-topic");
        msg.setTags("UNICOMCLOUD_ASSETS_7");
        msg.setKeys(assetsVO.getAssetsId());
        msg.setBody(JSON.toJSONString(assetsVO).getBytes(StandardCharsets.UTF_8));

        boolean b = sendMessage(msg);
        return CommonResult.success(b?"发送成功":"发送失败");
    }

    @ApiOperation( "字段资产上下线推送信息")
    @GetMapping("/fieldOnlie")
    public Object fieldOnlie(AssetsMsgVO assetsVO) {
        log.info("接收到的消息为--------> {}",assetsVO);
        Message msg = new Message();
        msg.setTopic("gd-unicom-topic");
        msg.setTags("UNICOMCLOUD_ASSETS_7");
        msg.setKeys(assetsVO.getAssetsId());
        msg.setBody(JSON.toJSONString(assetsVO).getBytes(StandardCharsets.UTF_8));

        boolean b = sendMessage(msg);
        return CommonResult.success(b?"发送成功":"发送失败");
    }

    @ApiOperation( "文件资产上下线推送信息")
    @GetMapping("/fileOnlie")
    public Object fileOnlie(AssetsMsgVO assetsVO) {
        Message msg = new Message();
        msg.setTopic("gd-unicom-topic");
        msg.setTags("UNICOMCLOUD_ASSETS_2");
        msg.setKeys(assetsVO.getAssetsId());
        msg.setBody(JSON.toJSONString(assetsVO).getBytes(StandardCharsets.UTF_8));
        boolean b = sendMessage(msg);
        return CommonResult.success(b?"发送成功":"发送失败");
    }

    @ApiOperation( "标签资产上下线推送信息")
    @PostMapping("/labelOnlie")
    public Object labelOnlie(@RequestBody AssetDataMsgVO assetsVO) {
        Message msg = new Message();
        msg.setTopic("adb-unicom-topic");
        msg.setTags("UNICOMCLOUD_ASSETS_5");
        msg.setKeys(assetsVO.getAssetsId());
        msg.setBody(JSON.toJSONString(assetsVO).getBytes(StandardCharsets.UTF_8));
        boolean b = sendMessage(msg);
        return CommonResult.success(b?"发送成功":"发送失败");
    }

    @ApiOperation( "分组信息")
    @PostMapping("/1")
    public Object grpInfo(@RequestBody List<GroupInfoMsgVO> groupInfoMsgVO) {
        Message msg = new Message();
        msg.setTopic("adb-tag-unicom-topic");
        msg.setTags("UNICOMCLOUD_ASSETS_TAG_7");
        msg.setBody(JSON.toJSONString(groupInfoMsgVO).getBytes(StandardCharsets.UTF_8));
        boolean b = sendMessage(msg);
        return CommonResult.success(b?"发送成功":"发送失败");
    }
    @ApiOperation( "码表信息")
    @PostMapping("/2")
    public Object dimInfo(@RequestBody DimInfoMsgVO dimInfoMsgVOS) {
        Message msg = new Message();
        msg.setTopic("adb-dim-unicom-topic");
        msg.setTags("UNICOMCLOUD_ASSETS_DIM_7");
        msg.setBody(JSON.toJSONString(dimInfoMsgVOS).getBytes(StandardCharsets.UTF_8));
        boolean b = sendMessage(msg);
        return CommonResult.success(b?"发送成功":"发送失败");
    }

    private boolean sendMessage(Message message) {
        log.info("开始发送消息, mqMessageDTO:{}", JSON.toJSONString(message));
        SendResult sendResult;
        try {
            sendResult = defaultMQProducer.send(message);
        } catch (Exception e) {
            log.error("消息发送失败, mqMessageDTO={}, cause:{}", JSON.toJSONString(message), e);
            return false;
        }
        if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
            log.info("发送成功, sendResult:{}", JSON.toJSONString(sendResult));
            return true;
        }

        return false;
    }

}
