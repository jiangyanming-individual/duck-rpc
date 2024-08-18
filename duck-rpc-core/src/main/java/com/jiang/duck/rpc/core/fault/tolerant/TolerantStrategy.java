package com.jiang.duck.rpc.core.fault.tolerant;

import com.jiang.duck.rpc.core.model.RpcResponse;

import java.util.Map;

/**
 * 容错机制
 */
public interface TolerantStrategy {

    RpcResponse doTolerant(Map<String, Object> context,Exception e);
}
