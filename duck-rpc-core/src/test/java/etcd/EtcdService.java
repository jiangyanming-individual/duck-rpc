package etcd;

import com.jiang.duck.rpc.core.config.RegisterConfig;
import com.jiang.duck.rpc.core.model.ServiceMetaInfo;
import com.jiang.duck.rpc.core.register.Register;
import com.jiang.duck.rpc.core.register.impl.EtcdRegister;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class EtcdService {


    final Register etcdRegister=new EtcdRegister();

    @Before
    public void testInit(){
        RegisterConfig registerConfig = new RegisterConfig();
        //注册中心地址
        registerConfig.setAddress("http://localhost:2379");
        //注册中心初始化：
        etcdRegister.init(registerConfig);
    }

    /**
     * 测试注册服务
     */
    @Test
    public void testRegister() throws Exception {
        //实例化服务类：
        ServiceMetaInfo serviceMetaInfo=new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);
        //注册服务
        etcdRegister.registerService(serviceMetaInfo);


        //实例化服务类：
        serviceMetaInfo=new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1235);
        //注册服务
        etcdRegister.registerService(serviceMetaInfo);


        //实例化服务类：
        serviceMetaInfo=new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("2.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1236);
        //注册服务
        etcdRegister.registerService(serviceMetaInfo);

    }

    /**
     *服务注销
     * @throws Exception
     */
    @Test
    public void unRegister() throws Exception {
        //实例化服务类：
        ServiceMetaInfo serviceMetaInfo=new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);
        //注册服务
        etcdRegister.unRegisterService(serviceMetaInfo);

    }

    /**
     * 服务发现
     * @throws Exception
     */
    @Test
    public void discoveryTest() throws Exception {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        String serviceKey = serviceMetaInfo.getServiceKey();

        List<ServiceMetaInfo> serviceMetaInfoList = etcdRegister.discoveryRegister(serviceKey);
        System.out.println(serviceMetaInfoList);
        Assert.assertNotNull(serviceMetaInfoList);
    }


    @Test
    public void heartbeatTest() throws Exception {
        //测试注册
        testRegister();
        //阻塞两分钟
        Thread.sleep(120 * 1000L);
    }

}
