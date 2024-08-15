package com.jiang.duck.rpc.core.retry.impl;

import com.github.rholder.retry.*;
import com.google.rpc.RetryInfoOrBuilder;
import com.jiang.duck.rpc.core.model.RpcResponse;
import com.jiang.duck.rpc.core.retry.RetryStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 固定时间间隔重试
 */
@Slf4j
public class FixIntervalRetryStrategy implements RetryStrategy {
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws ExecutionException,RetryException {

        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class) // 当出现异常时进行重试
                .withWaitStrategy(WaitStrategies.fixedWait(3L, TimeUnit.SECONDS)) // 重试等待时间
                .withStopStrategy(StopStrategies.stopAfterAttempt(3)) // 重试停止策略
                .withRetryListener(new RetryListener() { // 监听重试
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("重试次数：{}", attempt.getAttemptNumber());
                    }
                })
                .build();
        return retryer.call(callable);
    }
}
