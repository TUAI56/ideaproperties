package com.bonc.assetservice.apiserver.server.config;

import com.bonc.assetservice.apiserver.server.interceptor.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * WebMVC配置，你可以集中在这里配置拦截器、过滤器、静态资源缓存等
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Autowired
    RequestInterceptor requestInterceptor;

//    @Bean
//    public RequestInterceptor getRequestInterceptor() {
//        return new RequestInterceptor();
//    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestInterceptor).addPathPatterns("/**");
        super.addInterceptors(registry);
    }
}
