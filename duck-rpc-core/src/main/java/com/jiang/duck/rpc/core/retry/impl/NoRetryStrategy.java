package com.jiang.duck.rpc.core.retry.impl;

import com.jiang.duck.rpc.core.model.RpcResponse;
import com.jiang.duck.rpc.core.retry.RetryStrategy;

import java.util.concurrent.Callable;

/**
 * 不重试实现类：
 */
public class NoRetryStrategy implements RetryStrategy {


    /**
     * 不重试
     * @param callable
     * @return
     * @throws Exception
     */
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        return callable.call();
    }
}
