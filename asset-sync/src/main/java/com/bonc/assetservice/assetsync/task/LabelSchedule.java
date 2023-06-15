package com.bonc.assetservice.assetsync.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.bonc.assetservice.assetsync.constant.AssetConstant;
import com.bonc.assetservice.metadata.entity.MetaAssetInfo;
import com.bonc.assetservice.metadata.mapper.MetaAssetInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

//@Component
//@Configuration
//@EnableScheduling
//@Slf4j
//public class LabelSchedule {

//    @Autowired
//    private MetaAssetInfoMapper metaAssetInfoMapper;
//
//    @Scheduled(cron = "0 0 0 * * ?")
//    public void labelOnlineSchedule() {
//        log.info("标签资产上下线开始 -------->");
//        LambdaUpdateWrapper<MetaAssetInfo> metaAssetInfoWrapper = new LambdaUpdateWrapper<>();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            Date now = sdf.parse(sdf.format(new Date()));
//            //标签资产下线
//            metaAssetInfoWrapper.ge(MetaAssetInfo::getOfflineDate, now)
//                    .eq(MetaAssetInfo::getIsValid, AssetConstant.IS_VALID)
//                    .set(MetaAssetInfo::getIsValid, AssetConstant.IS_INVALID)
//                    .set(MetaAssetInfo::getOfflineDate, now);
//            int assetOfflineCount = metaAssetInfoMapper.update(null, metaAssetInfoWrapper);
//            log.info("资产下线条数：{}", assetOfflineCount);

            //        //标签资产上线
//        metaAssetInfoWrapper.le(MetaAssetInfo::getOnlineDate,now)
//                            .eq(MetaAssetInfo::getIsValid, AssetConstant.IS_VALID_INITIAL)
//                            .set(MetaAssetInfo::getIsValid,AssetConstant.IS_VALID);
//        int assetOnlineCount = metaAssetInfoMapper.update(null, metaAssetInfoWrapper);
//        log.info("资产上线条数：{}",assetOnlineCount);

//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//
//    }
//}
