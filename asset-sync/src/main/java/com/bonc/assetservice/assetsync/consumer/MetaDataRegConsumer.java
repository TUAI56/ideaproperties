package com.bonc.assetservice.assetsync.consumer;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.bonc.assetservice.assetsync.constant.ApiValueEnum;
import com.bonc.assetservice.assetsync.constant.AssetConstant;
import com.bonc.assetservice.assetsync.handler.IAssetRegisterHandler;
import com.bonc.assetservice.assetsync.model.AssetsMsgVO;
import com.bonc.assetservice.assetsync.model.AttributeDetailDataVO;
import com.bonc.assetservice.assetsync.util.GetUtil;
import com.bonc.assetservice.assetsync.util.UrlRequestFactory;
import com.bonc.assetservice.metadata.entity.MetaAssetMqLog;
import com.bonc.assetservice.metadata.entity.MetaModelTable;
import com.bonc.assetservice.metadata.mapper.MetaAssetMqLogMapper;
import com.bonc.assetservice.metadata.mapper.MetaModelTableMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class MetaDataRegConsumer {

    @Autowired
    private MetaAssetMqLogMapper metaAssetMqLogMapper;
    @Autowired
    private MetaModelTableMapper metaModelTableMapper;
    @Autowired
    private UrlRequestFactory urlfactory;

    @Autowired
    @Qualifier("tableOnlineHandler")
    private IAssetRegisterHandler tableOnlineHandler;

    @Autowired
    @Qualifier("tableOfflineHandler")
    private IAssetRegisterHandler tableOfflineHandler;

    @Autowired
    @Qualifier("fieldOnlineHandler")
    private IAssetRegisterHandler fieldOnlineHandler;

    @Autowired
    @Qualifier("fieldOfflineHandler")
    private IAssetRegisterHandler fieldOfflineHandler;

    /**
     * 消息消息队列的数据,进行资产上线,下线有关操作的处理
     * @param messageExt
     */
    public void assetRegister(MessageExt messageExt) {
        log.info("开始处理数据");
        String messageString = new String(messageExt.getBody());
        AssetsMsgVO assetsVO = JSONUtil.toBean(messageString, AssetsMsgVO.class);
        log.info("messageString==>" + messageString);


        MetaAssetMqLog msgLog = MetaAssetMqLog.builder().oid(null).msgId(messageExt.getMsgId()).status(assetsVO.getStatus())
                .classes(assetsVO.getClassesId()).createDate(new Date()).msg(messageString).build();
        try {
            // 插入MQ 的记录......
            metaAssetMqLogMapper.insert(msgLog);
        } catch (Exception e) {
            log.error("消息写入日志异常返回, 消息：" + messageString, e);
            return;
        }

        //oid使用注解IdType.ASSIGN_ID，会自动生成id
        String mqLogId = msgLog.getOid();
       try{
           //获取api资产详情
           AttributeDetailDataVO detailData = urlfactory.getAttributeDetail(assetsVO.getAssetsId());
           //修改消息信息 里面资产信息返回的报文
           LambdaUpdateWrapper<MetaAssetMqLog> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
           lambdaUpdateWrapper.eq(MetaAssetMqLog::getOid, mqLogId)
                              .set(MetaAssetMqLog::getAssetJson,detailData.getReturnJson());
           metaAssetMqLogMapper.update(null,lambdaUpdateWrapper);
           //判断是不是 模板表
           boolean checkTableTemple=checkTableTemple(assetsVO,detailData);
           if(!checkTableTemple){
               lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
               lambdaUpdateWrapper.eq(MetaAssetMqLog::getOid, mqLogId)
                       .set(MetaAssetMqLog::getUpdateDate,new Date())
                       .set(MetaAssetMqLog::getIsAllow,"0");
               metaAssetMqLogMapper.update(null,lambdaUpdateWrapper);
               return ;
           }
           //是模板表,就进行 模板表的上下线,字段的上下线
           switch (assetsVO.getClassesId() + assetsVO.getStatus()) {
               case AssetConstant.TABLE + AssetConstant.ONLINE:
                   tableOnlineHandler.assetHandle(assetsVO, detailData, mqLogId);
                   break;
               case AssetConstant.TABLE + AssetConstant.OFFLINE:
                   tableOfflineHandler.assetHandle(assetsVO, detailData, mqLogId);
                   break;
               case AssetConstant.FIELD + AssetConstant.ONLINE:
                   fieldOnlineHandler.assetHandle(assetsVO, detailData, mqLogId);
                   break;
               case AssetConstant.FIELD + AssetConstant.OFFLINE:
                   fieldOfflineHandler.assetHandle(assetsVO, detailData, mqLogId);
                   break;
               default:
           }

           lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
           lambdaUpdateWrapper.eq(MetaAssetMqLog::getOid, mqLogId)
                   .set(MetaAssetMqLog::getUpdateDate,new Date())
                   .set(MetaAssetMqLog::getIsAllow,"1");
           metaAssetMqLogMapper.update(null,lambdaUpdateWrapper);

       } catch (Exception e) {
           LambdaUpdateWrapper<MetaAssetMqLog> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
           lambdaUpdateWrapper.eq(MetaAssetMqLog::getOid, mqLogId)
                              .set(MetaAssetMqLog::getUpdateDate,new Date())
                              .set(MetaAssetMqLog::getIsAllow,"1")
                              .set(MetaAssetMqLog::getExceptionMsg,e.getMessage());
           metaAssetMqLogMapper.update(null,lambdaUpdateWrapper);
           e.printStackTrace();
       }
    }

    /**
     * 判断资产是不是 模板表,不是模板表就直接返回
     * @param assetsVO
     * @param detailData
     * @return
     */
    private boolean checkTableTemple(AssetsMsgVO assetsVO, AttributeDetailDataVO detailData) {
         // 资产请求 判断类型是表资产 还是 字段资产 ,判断是不是模版表,不是模板表就直接更新返回
        switch (assetsVO.getClassesId()){
            case AssetConstant.TABLE:
                //判断是否是模板表
                String isTemplate = GetUtil.getAttrValue(detailData.getAttrList(), ApiValueEnum.IS_TEMPLATE);
                return !StringUtils.isBlank(isTemplate) && (AssetConstant.IS_TEMPLATE.equals(isTemplate));
            case AssetConstant.FIELD:
             //TODO 如何判断字段属于模板表 需要讨论
               //判断这个字段在表里面存在......~~~
                MetaModelTable metaModelTable = metaModelTableMapper.selectByMoldCode(assetsVO.getTableAssetsId());
                return metaModelTable != null;
            default:
                return false;
        }
    }


}
