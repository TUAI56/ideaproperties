package com.bonc.assetservice.apiserver.server.assetquery.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bonc.assetservice.apiserver.data.entity.AsyncAssetqueryReq;
import com.bonc.assetservice.apiserver.server.assetquery.parser.ParserManager_v1;
import com.bonc.assetservice.apiserver.server.assetquery.parser.exception.ParseException;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.SqlBuilder_v1;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.DataModel_v1;
import com.bonc.assetservice.apiserver.server.assetquery.validator.ValidatorManager_v1;
import com.bonc.assetservice.apiserver.server.assetquery.validator.exception.ValidateException;
import com.bonc.assetservice.apiserver.server.assetquery.vo.AsyncAssetQueryReqVO_v1;
import com.bonc.assetservice.apiserver.server.assetquery.vo.SyncAssetQueryReqVO_v1;
import com.bonc.assetservice.apiserver.server.assetquery.vo.SyncAssetQueryRespVO;
import com.bonc.assetservice.apiserver.server.common.BaseController;
import com.bonc.assetservice.apiserver.server.config.RocketMqProperties;
import com.bonc.assetservice.apiserver.server.service.adb.AssetQueryService;
import com.bonc.assetservice.apiserver.server.service.apiserver.IAsyncAssetqueryReqService;
import com.bonc.framework.common.enums.Enums;
import com.bonc.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.bonc.framework.common.pojo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.select.PlainSelect;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * @Author: Ethan.Xing
 * @Date: 2022/6/13
 * @Description:
 *
 * TODO:
 *   1.增加controller多版本
 *   2.处理请求中reqId
 *   3.处理请求中provId：需要加到每一个关联表，提高查询效率
 *   4.当aggregation: true时，select中不添加device_number
 *   5.调整各个表的主键（user_id）、账期字段(data_id)，从配置中获取。
 *
 *

 *
 * DONE：
 *  *   4.查询结果中，增加device_number字段: 考虑在SQLBuilder中处理
 *  *   6.实时接口返回值中的data[][]修改为json个key-value
 *  *   9.查询标签中有域主表的标签时，如果标签的账期与域主表的最新账期不一样
 *  *     需要域主表最新账期 left join 域主表where acct = 标签账期
 *  *   7.横纵表组合筛选的实现逻辑：最多一个纵表（确认纵表字段）
 *  *
 *       8.一次请求中的所有标签，只能属于一个域
 */
@Api(value="标签取数相关接口",tags={"标签取数相关接口"})
@Slf4j
@RestController
@RequestMapping("/api/v1/query")
public class AssetQueryController_v1 extends BaseController {

    @Resource
    private RocketMqProperties propertiesProperties;
    @Autowired
    ValidatorManager_v1.ValidateSync validateSync;

    @Autowired
    ValidatorManager_v1.ValidateAsync validateAsync;

    @Autowired
    ParserManager_v1.SyncParseReq syncParseReq;
    @Autowired
    ParserManager_v1.AsyncParseReq asyncParseReq;
    @Resource(name = "assetQueryServiceImpl_v1")
    private AssetQueryService assetQueryService;

    @Resource
    private IAsyncAssetqueryReqService asyncAssetqueryReqService;

    @Resource
    private DefaultMQProducer defaultMQProducer;



    @Autowired
    SqlBuilder_v1 sqlBuilderV1;

    @ApiOperation("[标签取数] 实时查询数据")
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    public @ResponseBody CommonResult<SyncAssetQueryRespVO> syncGetData(HttpServletRequest httpReq,
                                                                        @Valid @RequestBody SyncAssetQueryReqVO_v1 req,
                                                                        BindingResult bindingResult) {

        String arriveMessage = "[标签取数] 实时查询数据收到请求: [" + httpReq.getSession().getId() + "]|[" + httpReq.getRemoteAddr()
                + "]|[" + httpReq.getMethod() + "]|[" + httpReq.getRequestURI() + "]|[" + JSONObject.toJSONString(req) + "]";
        log.info(arriveMessage);

        //会把校验失败情况下的反馈信息
        if (bindingResult.hasErrors()) {
            String retMsg = "入参校验失败，请联系管理员";
            if(bindingResult.getFieldError()!=null) {
                retMsg = bindingResult.getFieldError().getDefaultMessage();
            }
            log.warn(retMsg);
            return CommonResult.error(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), retMsg);
        }

        //从httpReq中获取天擎的AppId，并存入req
        CommonResult checkResult = getTqAppIdAndCheckPermission(httpReq,req);
        if (!checkResult.isSuccess()){
            return checkResult;
        }

        try {
            //入参校验
            validateSync.validate(req);

            //入参解析成内部结构
            DataModel_v1 dataModelV1 = syncParseReq.parser(req);

            //获取数据
            SyncAssetQueryRespVO ret = assetQueryService.getData(dataModelV1);

            return CommonResult.success(ret);
        } catch (ValidateException validateException) {
            log.warn("[标签取数] 实时查询数据接口 入参校验失败: " + validateException.getMessage(), validateException);
            return CommonResult.error(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), validateException.getMessage());
        } catch (ParseException parseException) {
            log.warn("[标签取数] 实时查询数据接口 入参解析失败: " + parseException.getMessage(), parseException);
            return CommonResult.error(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), parseException.getMessage());
        }catch (Exception exception){
            log.warn("[标签取数] 实时查询数据接口异常: " + exception.getMessage(), exception);
            return CommonResult.error(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getCode(), exception.getMessage());
        }
    }

    @ApiOperation("[标签取数] 下载数据")
    @RequestMapping(value = "/downFile", method = RequestMethod.POST)
    public @ResponseBody CommonResult<?> asyncGetData(HttpServletRequest httpReq,
                                                      @Valid @RequestBody AsyncAssetQueryReqVO_v1 req,
                                                      BindingResult bindingResult) {

        String arriveMessage = "[标签取数] 下载数据收到请求: [" + httpReq.getSession().getId() + "]|[" + httpReq.getRemoteAddr()
                + "]|[" + httpReq.getMethod() + "]|[" + httpReq.getRequestURI() + "]|[" + JSONObject.toJSONString(req) + "]";
        log.info(arriveMessage);

        //会把校验失败情况下的反馈信息
        if (bindingResult.hasErrors()) {
            String retMsg = "入参校验失败，请联系管理员";
            if (bindingResult.getFieldError() != null) {
                retMsg = bindingResult.getFieldError().getDefaultMessage();
            }
            log.warn(retMsg);
            return CommonResult.error(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), retMsg);
        }

        //从httpReq中获取天擎的AppId，并存入req
        CommonResult checkResult = getTqAppIdAndCheckPermission(httpReq,req);
        if (!checkResult.isSuccess()){
            return checkResult;
        }

        try {
            //入参校验
            validateAsync.validate(req);

            //入参解析成内部结构
            DataModel_v1 dataModelV1 = asyncParseReq.parser(req);
            //生成查询sql
            PlainSelect plainSelect = sqlBuilderV1.buildPlainSelect(dataModelV1);

            // 保存入参信息
            Long id = saveAsyncAssetqueryReq(req, AsyncAssetqueryReq.STATE_PENDING);

            // 将dataModel放入消息队列
            try {
                Map<String, String> map = new HashMap<>();
                map.put("id", String.valueOf(id));
                map.put("querySql", plainSelect.toString());
                map.put("tqAppUUId", req.getTqAppUUId());
                Message message = new Message(propertiesProperties.getTopic(), "", String.valueOf(id), JSONObject.toJSONBytes(map));
                SendResult sendResult = defaultMQProducer.send(message);
                if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                    log.info("[标签取数] 下载数据 发送消息队列完成");
                }
            } catch (Exception e) {
                log.error("[标签取数] 下载数据接口 发送消息队列失败：" + e.getMessage());
            }

            return CommonResult.success(null);
        } catch (ValidateException validateException) {
            log.warn("[标签取数] 下载数据接口 入参校验失败: " + validateException.getMessage(), validateException);
            saveAsyncAssetqueryReq(req, AsyncAssetqueryReq.STATE_VALIDATE_FAIL);
            return CommonResult.error(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), validateException.getMessage());
        } catch (ParseException parseException) {
            log.warn("[标签取数] 下载数据接口 入参解析失败: " + parseException.getMessage(), parseException);
            saveAsyncAssetqueryReq(req, AsyncAssetqueryReq.STATE_PARSE_FAIL);
            return CommonResult.error(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), parseException.getMessage());
        }
    }

    /**
     * 查询异步下发文件任务状态
     *
     * @param httpReq
     * @param reqId
     * @return
     */

    @ApiOperation("[标签取数] 实时异步下载状态")
    @RequestMapping(value = "/downresult", method = RequestMethod.GET)
    public @ResponseBody CommonResult queryAsyncAssetStatus(HttpServletRequest httpReq,@RequestParam String reqId, @RequestParam String downFileReqId){

        String tqAppUUId = httpReq.getHeader("appUUid");

        LambdaQueryWrapper<AsyncAssetqueryReq> wrapper = new LambdaQueryWrapper();
        wrapper.eq(AsyncAssetqueryReq::getReqId, downFileReqId);
        wrapper.eq(AsyncAssetqueryReq::getTqAppUuid, tqAppUUId);
        AsyncAssetqueryReq asyncAssetqueryReq = asyncAssetqueryReqService.getOne(wrapper);
        if(asyncAssetqueryReq == null){
            return CommonResult.error(-1, "未查询到downFileReqId:" + downFileReqId+"的异步查询信息,请核对下downFileReqId参数是否正确！");
        }
        Map map = new HashMap();
        map.put("status", Enums.AsyncStatus.getNameByValue(asyncAssetqueryReq.getState()));

            return CommonResult.success(map);
    }

    /**
     * 保存异步入参信息
     * @Author 李维帅
     * @Date 2022/7/5 16:06
     * @param req 入参信息
     * @param stats 状态
     * @return id
     **/
    private Long saveAsyncAssetqueryReq(AsyncAssetQueryReqVO_v1 req, Integer stats) {
        AsyncAssetqueryReq asyncAssetqueryReq = new AsyncAssetqueryReq();
        asyncAssetqueryReq.setReqId(req.getReqId());
        asyncAssetqueryReq.setTqAppUuid(req.getTqAppUUId());
        asyncAssetqueryReq.setQueryReq(JSONObject.toJSONString(req));
        asyncAssetqueryReq.setResourceId(Long.parseLong(req.getDestFtp().getResourceId()));
        asyncAssetqueryReq.setFileName(req.getDestFtp().getFileName());
        asyncAssetqueryReq.setFilePath(req.getDestFtp().getFilePath());
        asyncAssetqueryReq.setFileType(req.getDestFtp().getFileType());
        asyncAssetqueryReq.setSeparateChar(req.getDestFtp().getSeparator());
        asyncAssetqueryReq.setState(stats);
        asyncAssetqueryReq.setCrtTime(new Date());
        asyncAssetqueryReqService.saveOrUpdate(asyncAssetqueryReq);
        return asyncAssetqueryReq.getId();
    }
}
