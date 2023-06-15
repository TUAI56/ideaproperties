package com.bonc.assetservice.assetsync.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;


@Slf4j
@Configuration
public class AsyncPoolConfig implements AsyncConfigurer {

    /**
     * 并行线程数
     */
    public static final int POOL_SIZE;

    static {
        POOL_SIZE = Integer.max(Runtime.getRuntime().availableProcessors(), 5);
    }

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程池大小
        executor.setCorePoolSize(POOL_SIZE);
        //最大线程数
        executor.setMaxPoolSize(20);
        //队列容量
        executor.setQueueCapacity(50);
        //活跃时间
        executor.setKeepAliveSeconds(30);
        //线程名字前缀
        executor.setThreadNamePrefix("AsyncTaskExecutor-");

        // setRejectedExecutionHandler：当pool已经达到max size的时候，如何处理新任务
        // CallerRunsPolicy：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }


    /**
     *  异步任务中异常处理
     * @return
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {

        return (Throwable ex, Method method, Object... params) -> {
            String errorBuilder = "Async execution error on method:" + method.getName() + " with parameters:"
                    + Arrays.toString(params);
            log.error(errorBuilder, ex);
        };
    }
}
