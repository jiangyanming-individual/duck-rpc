package loadBalancer;


import com.jiang.duck.rpc.core.loadbalancer.impl.ConsistentHashLoadBalancer;
import com.jiang.duck.rpc.core.loadbalancer.impl.RandomLoadBalancer;
import com.jiang.duck.rpc.core.model.ServiceMetaInfo;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class loadbalancerTest {

//  ConsistentHashLoadBalancer loadBalancer=  new ConsistentHashLoadBalancer();

  RandomLoadBalancer loadBalancer=new RandomLoadBalancer();

  @Test
  public void test(){

    Map<String,Object> requestParams=new HashMap<>();

    requestParams.put("methodName","apple");

    //服务列表：
    ServiceMetaInfo serviceMetaInfo1 = new ServiceMetaInfo();
    serviceMetaInfo1.setServiceName("myService");
    serviceMetaInfo1.setServiceVersion("1.0");
    serviceMetaInfo1.setServiceHost("localhost");
    serviceMetaInfo1.setServicePort(1234);


    ServiceMetaInfo serviceMetaInfo2 = new ServiceMetaInfo();
    serviceMetaInfo2.setServiceName("myService");
    serviceMetaInfo2.setServiceVersion("1.0");
    serviceMetaInfo2.setServiceHost("yupi.icu");
    serviceMetaInfo2.setServicePort(8089);
    // 连续调用三次
    List<ServiceMetaInfo> serviceMetaInfoList = Arrays.asList(serviceMetaInfo1, serviceMetaInfo2);
    ServiceMetaInfo serviceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
    System.out.println(serviceMetaInfo);
    Assert.assertNotNull(serviceMetaInfo);

     serviceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
    System.out.println(serviceMetaInfo);
    Assert.assertNotNull(serviceMetaInfo);

     serviceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
    System.out.println(serviceMetaInfo);
    Assert.assertNotNull(serviceMetaInfo);

  }

}
