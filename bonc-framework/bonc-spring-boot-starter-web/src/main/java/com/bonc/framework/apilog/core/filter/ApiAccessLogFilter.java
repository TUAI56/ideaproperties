package com.bonc.framework.apilog.core.filter;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.bonc.framework.apilog.core.service.ApiAccessLogFrameworkService;
import com.bonc.framework.apilog.core.service.dto.ApiAccessLogCreateReqDTO;
import com.bonc.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.bonc.framework.common.pojo.CommonResult;
import com.bonc.framework.common.util.date.DateUtils;
import com.bonc.framework.common.util.monitor.TracerUtils;
import com.bonc.framework.common.util.servlet.ServletUtils;
import com.bonc.framework.web.config.WebProperties;
import com.bonc.framework.web.core.filter.ApiRequestFilter;
import com.bonc.framework.web.core.util.WebFrameworkUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import static com.bonc.framework.common.util.json.JsonUtils.toJsonString;

/**
 * API 访问日志 Filter
 *
 * @author 芋道源码
 */
@Slf4j
public class ApiAccessLogFilter extends ApiRequestFilter {

    private final String applicationName;

    private final ApiAccessLogFrameworkService apiAccessLogFrameworkService;

    public ApiAccessLogFilter(WebProperties webProperties, String applicationName, ApiAccessLogFrameworkService apiAccessLogFrameworkService) {
        super(webProperties);
        this.applicationName = applicationName;
        this.apiAccessLogFrameworkService = apiAccessLogFrameworkService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 获得开始时间
        Date beginTim = new Date();
        // 提前获得参数，避免 XssFilter 过滤处理
        Map<String, String> queryString = ServletUtil.getParamMap(request);
        String requestBody = ServletUtils.isJsonRequest(request) ? ServletUtil.getBody(request) : null;

        try {
            // 继续过滤器
            filterChain.doFilter(request, response);
            // 正常执行，记录日志
            createApiAccessLog(request, beginTim, queryString, requestBody, null);
        } catch (Exception ex) {
            // 异常执行，记录日志
            createApiAccessLog(request, beginTim, queryString, requestBody, ex);
            throw ex;
        }
    }

    private void createApiAccessLog(HttpServletRequest request, Date beginTime,
                                    Map<String, String> queryString, String requestBody, Exception ex) {
        ApiAccessLogCreateReqDTO accessLog = new ApiAccessLogCreateReqDTO();
        try {
            this.buildApiAccessLogDTO(accessLog, request, beginTime, queryString, requestBody, ex);
            apiAccessLogFrameworkService.createApiAccessLogAsync(accessLog);
        } catch (Throwable th) {
            log.error("[createApiAccessLog][url({}) log({}) 发生异常]", request.getRequestURI(), toJsonString(accessLog), th);
        }
    }

    private void buildApiAccessLogDTO(ApiAccessLogCreateReqDTO accessLog, HttpServletRequest request, Date beginTime,
                                      Map<String, String> queryString, String requestBody, Exception ex) {
        // 处理用户信息
        accessLog.setUserId(WebFrameworkUtils.getLoginUserId(request));
        accessLog.setUserType(WebFrameworkUtils.getLoginUserType(request));
        // 设置访问结果
        CommonResult<?> result = WebFrameworkUtils.getCommonResult(request);
        if (result != null) {
            accessLog.setResultCode(result.getCode());
            accessLog.setResultMsg(result.getMsg());
        } else if (ex != null) {
            accessLog.setResultCode(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getCode());
            accessLog.setResultMsg(ExceptionUtil.getRootCauseMessage(ex));
        } else {
            accessLog.setResultCode(0);
            accessLog.setResultMsg("");
        }
        // 设置其它字段
        accessLog.setTraceId(TracerUtils.getTraceId());
        accessLog.setApplicationName(applicationName);
        accessLog.setRequestUrl(request.getRequestURI());
        Map<String, Object> requestParams = MapUtil.<String, Object>builder().put("query", queryString).put("body", requestBody).build();
        accessLog.setRequestParams(toJsonString(requestParams));
        accessLog.setRequestMethod(request.getMethod());
        accessLog.setUserAgent(ServletUtils.getUserAgent(request));
        accessLog.setUserIp(ServletUtil.getClientIP(request));
        // 持续时间
        accessLog.setBeginTime(beginTime);
        accessLog.setEndTime(new Date());
        accessLog.setDuration((int) DateUtils.diff(accessLog.getEndTime(), accessLog.getBeginTime()));
    }

}
