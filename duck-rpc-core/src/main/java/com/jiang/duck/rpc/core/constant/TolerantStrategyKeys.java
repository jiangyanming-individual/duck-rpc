package com.jiang.duck.rpc.core.constant;


/**
 * 负载均衡器常量
 */
public interface TolerantStrategyKeys {


    /**
     * 故障恢复
     */
    String FAIL_BACK = "failBack";

    /**
     * 故障转移
     */
    String FAIL_OVER = "failOver";

    /**
     * 故障静默
     */
    String FAIL_SAFE = "failSafe";

    /**
     * 快速失败
     */
    String FAIL_FAST = "failFast";
}
