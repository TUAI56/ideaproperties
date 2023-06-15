package com.bonc.assetservice.apiserver.server.common;

import com.bonc.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.bonc.framework.common.pojo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Ethan.Xing
 * @Date: 2022/11/22
 * @Description: 定义api-server中controller中公共的处理逻辑
 */

@Slf4j
public abstract class BaseController {
    private static Map<String, String> appIdProv = new HashMap<>();

    static {
        appIdProv.put("quanke", "099");     //全客
        appIdProv.put("zhegnqi", "099");    //政企
    }

    /**
     * 获取天擎的appUUId，并存入AbstractBaseReqVO
     *
     * @param httpReq
     * @param req
     * @return
     */
    protected CommonResult getTqAppIdAndCheckPermission(HttpServletRequest httpReq, AbstractBaseReqVO req) {

        //获取天擎appUUId
        String tqAppUUId = httpReq.getHeader("appUUid");
        if (StringUtils.isBlank(tqAppUUId)) {
            log.error("请求报文中不携带天擎appUUId，异常");

            return CommonResult.error(GlobalErrorCodeConstants.TQ_APPID_NOT_EXISTS.getCode(),
                    GlobalErrorCodeConstants.TQ_APPID_NOT_EXISTS.getMsg());
        }
        req.setTqAppUUId(tqAppUUId);

        //TODO：暂时删除检查appUUId权限的逻辑，等后续跟天擎对接后再打开
//        //检查appUUId的省份信息，与provId是或冲突
//        String appProvId = appIdProv.get(tqAppUUId);
//        if (StringUtils.isBlank(appProvId)) {
//            log.error("请求报文中的appUUId:{}在api-server中未配置", tqAppUUId);
//            return CommonResult.error(GlobalErrorCodeConstants.TQ_APPID_NOT_CONFIG.getCode(),
//                    GlobalErrorCodeConstants.TQ_APPID_NOT_CONFIG.getMsg());
//        }
//
//        //如果appId不是全国的权限，并且appId关联的省份与req中的省份不相同，则不允许查询
//        if (!"099".equals(appProvId) && !appProvId.equals(req.getProvId())) {
//            log.error("请求报文中appUUId[{}]的权限是:{}，但是请求报文中的prov_id是:{}，权限校验失败", tqAppUUId, appProvId, appProvId);
//            return CommonResult.error(GlobalErrorCodeConstants.TQ_APP_QUERY_NOT_PERMISSION.getCode(),
//                    GlobalErrorCodeConstants.TQ_APP_QUERY_NOT_PERMISSION.getMsg());
//        }

        return CommonResult.success(null);
    }
}
