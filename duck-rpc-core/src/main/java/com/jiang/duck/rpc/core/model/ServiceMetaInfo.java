package com.jiang.duck.rpc.core.model;


import cn.hutool.core.util.StrUtil;
import lombok.Data;


/**
 * 服务节点信息类
 */
@Data
public class ServiceMetaInfo {
    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务版本
     */
    private String serviceVersion="1.0";
    /**
     * 服务域名: http://localhost
      */
    private String serviceHost;

    /** 8080
     * 服务端口号:
     */
    private Integer servicePort;


    /**
     * 服务分组
     */
    private String serviceGroup="default";


    /**
     * 得到服务注册键名
     */
    public String getServiceKey(){
        // xxx:xxx
        return String.format("%s:%s",serviceName,serviceVersion);
    }


    /**
     *得到服务注册节点名
     */
    public String getServiceNodeKey(){
        // xxx:xxx/xxx:xxx
        return String.format("%s/%s:%s",getServiceKey(),serviceHost,servicePort);
    }

    /**
     * 获取服务节点地址：
     * @return
     */
    public String getServiceAddress(){
        //拼接域名和端口号
        if (!StrUtil.contains(serviceHost,"http")){
            return String.format("http://"+"%s:%s",serviceHost,servicePort);
        }
        return String.format("%s:%s",serviceHost,servicePort);
    }
}

