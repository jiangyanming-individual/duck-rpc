package com.jiang.duck.rpc.core.fault.tolerant.impl;

import com.jiang.duck.rpc.core.fault.tolerant.TolerantStrategy;
import com.jiang.duck.rpc.core.model.RpcResponse;

import java.util.Map;

/**
 * 快速失败策略
 */
public class FailFastTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        throw  new RuntimeException("服务报错",e);
    }
}
