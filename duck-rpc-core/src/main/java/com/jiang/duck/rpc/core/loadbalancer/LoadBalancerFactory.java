package com.jiang.duck.rpc.core.loadbalancer;
import com.jiang.duck.rpc.core.loadbalancer.impl.RoundRobinLoadBalancer;
import com.jiang.duck.rpc.core.serializer.Serializer;
import com.jiang.duck.rpc.core.serializer.impl.JdkSerializer;
import com.jiang.duck.rpc.core.spi.SpiLoader;

/**
 * 根据SPI生成负载均衡器
 */
public class LoadBalancerFactory {

    /**
     * 静态代码块，会随着类的加载而加载：仅会加载一次，因为是静态的，属于类。不属于对象。
     */
    static {
        SpiLoader.load(LoadBalancer.class);
    }

    /**
     * 默认负载均衡器
     */
    private static final LoadBalancer DEFAULT_LOADBALANCER = new RoundRobinLoadBalancer();

    /**
     * 获取实例负载均衡器
     * @param key
     * @return
     */
    public static LoadBalancer getInstance(String key){

        return SpiLoader.getInstance(LoadBalancer.class,key);
    }

}
