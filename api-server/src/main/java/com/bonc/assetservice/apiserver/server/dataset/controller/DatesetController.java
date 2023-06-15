package com.bonc.assetservice.apiserver.server.dataset.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bonc.assetservice.apiserver.data.entity.UserDatasetColInfo;
import com.bonc.assetservice.apiserver.data.entity.UserDatasetUpdReq;
import com.bonc.assetservice.apiserver.server.common.BaseController;
import com.bonc.assetservice.apiserver.server.config.RocketMqProperties;
import com.bonc.assetservice.apiserver.server.dataset.service.IDatasetAdbService;
import com.bonc.assetservice.apiserver.server.dataset.service.IDatasetService;
import com.bonc.assetservice.apiserver.server.dataset.vo.Column;
import com.bonc.assetservice.apiserver.server.dataset.vo.DatasetCreateReqVo;
import com.bonc.assetservice.apiserver.server.dataset.vo.DatasetupdateReqVo;
import com.bonc.assetservice.apiserver.server.service.apiserver.IUserDatasetUpdReqService;
import com.bonc.framework.common.enums.Enums;
import com.bonc.framework.common.exception.ErrorCode;
import com.bonc.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.bonc.framework.common.pojo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import static com.bonc.assetservice.metadata.constant.MetaDataConst.DATE_FORMAT_YYYYMMDD;
import static com.bonc.assetservice.metadata.constant.MetaDataConst.DB_DEL_FLAG_N;

@Api(value = "自定义数据集相关接口", tags = {"自定义数据集相关接口"})
@Slf4j
@RestController
@RequestMapping("/userdataset")
public class DatesetController extends BaseController {

    @Resource
    private IDatasetService datasetService;
    @Resource
    private IDatasetAdbService datasetAdbService;
    @Resource
    private IUserDatasetUpdReqService userDatasetUpdReqService;

    @Resource
    private DefaultMQProducer defaultMQProducer;
    @Resource
    private RocketMqProperties rocketMqProperties;
    @Value("${dataset.create.defaultexpiredate}")
    private int defaultExpireDate;
    @Value("${dataset.create.maxexpiredate}")
    private int maxExpireDate;

    @ApiOperation("[应用系统自定义数据集] 创建数据集")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody CommonResult<?> createUserDataset(HttpServletRequest httpReq,
                                                           @Validated @RequestBody DatasetCreateReqVo req,
                                                           BindingResult bindingResult) {

        String arriveMessage = "[应用系统自定义数据集] 创建数据集收到请求: [" + httpReq.getSession().getId() + "]|[" + httpReq.getRemoteAddr()
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
        CommonResult commonResult = validatepara(req);
        if (commonResult != null) {
            return commonResult;
        }
        //获取tqAppUuid
        CommonResult checkResult = getTqAppIdAndCheckPermission(httpReq, req);
        if (!checkResult.isSuccess()){
            return checkResult;
        }

        String tableName = "dataset_" + req.getDatasetId();

        Map columnListAndSql = getListAndSql(req, tableName);
        //创建adb数据集表
        datasetAdbService.exeSqlInAdb(columnListAndSql.get("sql").toString());

        try {
            datasetService.saveDateset(req, (List<UserDatasetColInfo>) columnListAndSql.get("columnList"), tableName);

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            //保存数据失败 删除创建的自定义数据集表
            String deleteTabelSql = "drop table " + tableName;
            datasetAdbService.exeSqlInAdb(deleteTabelSql);
            return CommonResult.error(new ErrorCode(-1, e.getMessage()));
        }

        return CommonResult.success(new ErrorCode(0, "数据集创建成功！"));
    }

    @ApiOperation("[应用系统自定义数据集] 更新数据-触发")
    @RequestMapping(value = "/upddata", method = RequestMethod.POST)
    public @ResponseBody CommonResult<?> userDatasetUpdDate(HttpServletRequest httpReq,
                                                      @Valid @RequestBody DatasetupdateReqVo req,
                                                      BindingResult bindingResult) {

        String arriveMessage = "[应用系统自定义数据集] 更新数据-触发收到请求: [" + httpReq.getSession().getId() + "]|[" + httpReq.getRemoteAddr()
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
            //保存user_dataset_upd_req
            String id = updateReqStatus(req, UserDatasetUpdReq.STATE_WAITING);
            log.info("[应用系统自定义数据集] 更新数据-触发 保存请求参数完成");
            // 将请求放入消息队列
            Map<String, String> map = new HashMap<>();
            map.put("id", id);
            map.put("tqAppUUId", req.getTqAppUUId());
            Message message = new Message(rocketMqProperties.getDatasetupddatatopic(), "", req.getReqId(), JSONObject.toJSONBytes(map));
            SendResult sendResult = defaultMQProducer.send(message);
            if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                log.info("[应用系统自定义数据集] 更新数据-触发 发送消息队列完成");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[应用系统自定义数据集] 更新数据-触发 发送消息队列失败：" + e.getMessage());
        }
       return CommonResult.success(GlobalErrorCodeConstants.SUCCESS);
    }

    /**
     * 查询异步下发文件任务状态
     *
     * @param httpReq
     * @param reqId
     * @return
     */

    @ApiOperation("[标签取数] 实时异步下载状态")
    @RequestMapping(value = "/updresult", method = RequestMethod.GET)
    public @ResponseBody CommonResult queryUserDatasetUpdStatus(HttpServletRequest httpReq,@RequestParam String reqId,@RequestParam String updDataReqId,
                                                                @RequestParam String provId, @RequestParam String datasetId){

        String tqAppUUId = httpReq.getHeader("appUUid");

        LambdaQueryWrapper<UserDatasetUpdReq> wrapper = new LambdaQueryWrapper();
        wrapper.eq(UserDatasetUpdReq::getDatasetId, datasetId);
        wrapper.eq(UserDatasetUpdReq::getTqAppUuid, tqAppUUId);
        wrapper.eq(UserDatasetUpdReq::getProvId, provId);
        wrapper.eq(UserDatasetUpdReq::getReqId, updDataReqId);
        UserDatasetUpdReq userDatasetUpdReq = userDatasetUpdReqService.getOne(wrapper);
        Map map = new HashMap();
        map.put("status", Enums.UpdDatasetStatus.getNameByValue(userDatasetUpdReq.getState()));

        return CommonResult.success(map);
    }




    private CommonResult validatepara(DatasetCreateReqVo req) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date paramDate = null;
        if (StringUtils.isEmpty(req.getExpireDate())) {
            req.setExpireDate(sdf.format(DateUtils.addDays(new Date(), defaultExpireDate)));
        } else {

            if (!Pattern.matches(DATE_FORMAT_YYYYMMDD, req.getExpireDate())) {
                return CommonResult.error(new ErrorCode(-2, "用户自定义数据集的失效时间的格式应为'yyyyMMdd'格式，请核对！"));
            }
            try {
                req.setExpireDate(DateUtils.addDays(new Date(), maxExpireDate).after(sdf.parse(req.getExpireDate())) ? req.getExpireDate() : sdf.format(DateUtils.addDays(new Date(), maxExpireDate)));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

        }
        if (req.getColumns().isEmpty()) {
            return CommonResult.error(new ErrorCode(-3, "应用系统自定义数据集字段列表为空，请核对！"));
        }
        return null;
    }

    private Map getListAndSql(DatasetCreateReqVo datasetCreateReqVo, String tableName){
        List<UserDatasetColInfo> columnList = new ArrayList<UserDatasetColInfo>() ;
        StringBuffer  createTableSql = new StringBuffer("create table " + tableName +" (");
        //参数中的columns转换成表实体对象，拼接创建数据集表sql
        for(Column column : datasetCreateReqVo.getColumns()){
            UserDatasetColInfo userDatasetColInfo = new UserDatasetColInfo();
            userDatasetColInfo.setDatasetId(datasetCreateReqVo.getDatasetId());
            userDatasetColInfo.setColCode(column.getCode());
            userDatasetColInfo.setColName(column.getName());
            userDatasetColInfo.setColType(column.getType());
            userDatasetColInfo.setColLength(column.getLength());
            userDatasetColInfo.setColDesc(column.getDesc());
            userDatasetColInfo.setCrtTime(new Date());
            userDatasetColInfo.setDelFlag(DB_DEL_FLAG_N);
            columnList.add(userDatasetColInfo);
            createTableSql.append(column.getCode() + " ");
            createTableSql.append(column.getType() + " ");
            createTableSql.append("("+ column.getLength()+"),");
        }
        createTableSql.deleteCharAt(createTableSql.length()-1);
        createTableSql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
        Map listAndSql = new HashMap<>();
        listAndSql.put("sql", createTableSql.toString());
        listAndSql.put("columnList", columnList);

        return listAndSql;
    }

    private String updateReqStatus(DatasetupdateReqVo req, Integer status){
        UserDatasetUpdReq userDatasetUpdReq = new UserDatasetUpdReq();
        BeanUtils.copyProperties(req, userDatasetUpdReq);
        userDatasetUpdReq.setTqAppUuid(req.getTqAppUUId());
        userDatasetUpdReq.setReqId(req.getReqId());
        userDatasetUpdReq.setProvId(req.getProvId());
        userDatasetUpdReq.setDatasetId(req.getDatasetId());
        userDatasetUpdReq.setFileName(req.getSrcFtp().getFileName());
        userDatasetUpdReq.setFilePath(req.getSrcFtp().getFilePath());
        userDatasetUpdReq.setResourceId(Long.valueOf(req.getSrcFtp().getResourceId()));
        userDatasetUpdReq.setCrtTime(new Date());
        userDatasetUpdReq.setState(status);

        userDatasetUpdReqService.saveOrUpdate(userDatasetUpdReq);
        return String.valueOf(userDatasetUpdReq.getId());
    }

}
