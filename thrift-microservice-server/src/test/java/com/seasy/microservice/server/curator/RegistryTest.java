package com.seasy.microservice.server.curator;

import java.util.concurrent.TimeUnit;

import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceType;

import com.seasy.microservice.core.ServiceRegistry;
import com.seasy.microservice.core.ThriftServiceRegistry;
import com.seasy.microservice.core.common.ThriftServicePayload;
import com.seasy.microservice.core.utils.EnvUtil;

public class RegistryTest extends BaseTest{
	public static void main(String[] args) throws Exception {
//		startTestingServer();
		
	    //服务注册器
	    ServiceRegistry serviceRegistry = new ThriftServiceRegistry(connectString, ServiceRegistry.ZNODE_PATH_SERVICE);
	    serviceRegistry.start();
		
		//注册服务实例
		ServiceInstance<ThriftServicePayload> serviceInstance = ServiceInstance.<ThriftServicePayload>builder()
				.name("Hello")
				.id("")
				.address(EnvUtil.getLocalIp())
				.port(20001)
				.payload(new ThriftServicePayload("1.0.0", "com.seasy.microservice.api.Hello"))
				.registrationTimeUTC(System.currentTimeMillis())
				.serviceType(ServiceType.DYNAMIC)
				.build();
		
		serviceRegistry.registerService(serviceInstance);
		
//		serviceRegistry.close();
//		stopTestingServer();
		
		TimeUnit.MINUTES.sleep(10);
	}
	
}
