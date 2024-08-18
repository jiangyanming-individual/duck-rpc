package com.jiang.duck.rpc.core.fault.tolerant.impl;

import com.jiang.duck.rpc.core.fault.tolerant.TolerantStrategy;
import com.jiang.duck.rpc.core.model.RpcResponse;

import java.util.Map;

/***
 * 故障转移：
 */
public class FailOverTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        return null;
    }
}
