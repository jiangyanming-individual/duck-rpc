package com.jiang.duck.rpc.core.fault.tolerant;

import com.jiang.duck.rpc.core.fault.tolerant.impl.FailFastTolerantStrategy;
import com.jiang.duck.rpc.core.fault.tolerant.impl.FailSafeTolerantStrategy;
import com.jiang.duck.rpc.core.loadbalancer.LoadBalancer;
import com.jiang.duck.rpc.core.loadbalancer.impl.RoundRobinLoadBalancer;
import com.jiang.duck.rpc.core.spi.SpiLoader;

/**
 * 根据SPI生成容错器
 */
public class TolerantFactory {

    /**
     * 静态代码块，会随着类的加载而加载：仅会加载一次，因为是静态的，属于类。不属于对象。
     */
    static {
        SpiLoader.load(TolerantStrategy.class);
    }

    /**
     * 默认容错器：快速失败
     */
    private static final TolerantStrategy DEFAULT_TOLERANT = new FailFastTolerantStrategy();

    /**
     * 获取实例容错实例
     *
     * @param key
     * @return
     */
    public static TolerantStrategy getInstance(String key) {

        return SpiLoader.getInstance(TolerantStrategy.class, key);
    }

}
