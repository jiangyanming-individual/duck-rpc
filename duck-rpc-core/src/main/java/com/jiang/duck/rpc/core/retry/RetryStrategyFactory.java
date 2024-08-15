package com.jiang.duck.rpc.core.retry;
import com.jiang.duck.rpc.core.loadbalancer.LoadBalancer;
import com.jiang.duck.rpc.core.loadbalancer.impl.RoundRobinLoadBalancer;
import com.jiang.duck.rpc.core.retry.impl.NoRetryStrategy;
import com.jiang.duck.rpc.core.spi.SpiLoader;

/**
 * 根据SPI生成重试策略
 */
public class RetryStrategyFactory {

    /**
     * 静态代码块，会随着类的加载而加载：仅会加载一次，因为是静态的，属于类。不属于对象。
     */
    static {
        SpiLoader.load(RetryStrategy.class);
    }

    /**
     * 默认的重试策略
     */
    private static final RetryStrategy DEFAULT_RETRYSTRATEGY = new NoRetryStrategy();

    /**
     * 获取重试策略
     * @param key
     * @return
     */
    public static RetryStrategy getInstance(String key){

        return SpiLoader.getInstance(RetryStrategy.class,key);
    }

}
