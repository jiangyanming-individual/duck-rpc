package com.jiang.duck.rpc.core.fault.tolerant.impl;

import com.jiang.duck.rpc.core.fault.tolerant.TolerantStrategy;
import com.jiang.duck.rpc.core.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 静默失败，默认失败不处理--容错策略
 */
@Slf4j
public class FailSafeTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.info("服务调用失败");
        return new RpcResponse();
    }
}
