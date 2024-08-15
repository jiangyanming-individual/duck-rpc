package com.jiang.duck.rpc.core.retry;

import com.jiang.duck.rpc.core.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 重试机制的接口
 */
public interface RetryStrategy {

    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;
}
