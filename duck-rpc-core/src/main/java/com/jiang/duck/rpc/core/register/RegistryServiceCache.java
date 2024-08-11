package com.jiang.duck.rpc.core.register;

import com.jiang.duck.rpc.core.model.ServiceMetaInfo;

import java.util.List;

/**
 * 消费者服务缓存
 */
public class RegistryServiceCache {


    List<ServiceMetaInfo> serviceCache;

    /**
     * 写缓存
     * @param newServiceCache
     */
    public void writeCache(List<ServiceMetaInfo> newServiceCache){

        this.serviceCache=newServiceCache;
    }

    /**
     * 读缓存
     * @return
     */

    public List<ServiceMetaInfo> readCache(){
        return this.serviceCache;
    }

    /**
     * 清理缓存
     */
    public void clearCache(){
        this.serviceCache=null;
    }

}
