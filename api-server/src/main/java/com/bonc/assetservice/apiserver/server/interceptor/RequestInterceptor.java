package com.bonc.assetservice.apiserver.server.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.bonc.assetservice.apiserver.data.entity.ReqInfo;
import com.bonc.assetservice.apiserver.server.service.apiserver.IReqInfoService;
import com.bonc.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.bonc.framework.common.pojo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * <p>
 * 请求参数处理拦截器
 * </p>
 *
 * @author zhaozesheng
 * @since 2022-12-22 10:19:02
 */
@Slf4j
@Component
public class RequestInterceptor extends HandlerInterceptorAdapter {


    @Resource
    IReqInfoService reqInfoService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //获取请求body

        String reqId = "";
        String body = "";
        if (RequestMethod.GET.toString().equals(request.getMethod())) {
            reqId = request.getParameter("reqId");
            body = request.getQueryString();
        } else {
            byte[] bodyBytes = StreamUtils.copyToByteArray(request.getInputStream());
            body = new String(bodyBytes, request.getCharacterEncoding());
            log.info("请求参数：{}", body);
            if (StringUtils.isEmpty(body)){
                writeErrorResp(response, "获取参数信息为空");
            }
            JSONObject jsonReq = JSONObject.parseObject(body);
            reqId = jsonReq.getString("reqId");
        }

        //reqId值为空
        if (StringUtils.isEmpty(reqId)) {
            writeErrorResp(response, "reqId不允许为空");
            return false;
        }

        log.info("reqId值是：" + reqId);
        //插入数据库
        try {
            ReqInfo reqInfo = new ReqInfo();
            reqInfo.setReqId(reqId);
            reqInfo.setReqBody(body);
            reqInfo.setCrtTime(new Date());
            reqInfoService.save(reqInfo);
            return true;
        }catch (Exception e){
            log.error("reqId插入数据库错误",e);
            //设置response状态
            writeErrorResp(response, "reqId重复!");
            return false;
        }
    }

    private static void writeErrorResp(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = null ;
        out = response.getWriter();

        out.write(JSONObject.toJSONString(CommonResult.error(GlobalErrorCodeConstants.BAD_REQUEST.getCode(),msg)));
        out.flush();
        out.close();
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
