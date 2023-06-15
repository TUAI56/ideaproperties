package com.bonc.assetservice.apiserver.server.service.metadata;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bonc.assetservice.metadata.entity.MetaAssetDate;

import java.util.List;

/**
 * @Description: meta_asset_date
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
public interface IMetaAssetDateService extends IService<MetaAssetDate> {

    /**
     * <p>
     * 根据标签ID查询所有的账期
     * </p>
     *
     * @author zhaozesheng
     * @since 2022-12-21 14:17:42
     */
    List<String> getAssetAcctList(Long assetId);
}
