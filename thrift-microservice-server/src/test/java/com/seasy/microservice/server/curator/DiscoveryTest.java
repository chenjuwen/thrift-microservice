package com.seasy.microservice.server.curator;

import java.util.Collection;

import org.apache.curator.x.discovery.ServiceInstance;

import com.seasy.microservice.core.ServiceRegistry;
import com.seasy.microservice.core.ThriftServiceRegistry;
import com.seasy.microservice.core.common.ThriftServicePayload;

public class DiscoveryTest extends BaseTest{
	public static void main(String[] args) throws Exception {
//		startTestingServer();

		ServiceRegistry serviceRegistry = new ThriftServiceRegistry(connectString, ServiceRegistry.ZNODE_PATH_SERVICE);
	    serviceRegistry.start();
	    
	    Collection<String> nameList = serviceRegistry.queryForNames();
	    for(String serviceName : nameList){
	    	System.out.println(serviceName);
	    	
	    	Collection<ServiceInstance<ThriftServicePayload>> instanceList = serviceRegistry.queryForInstances(serviceName);
	    	if(instanceList != null && instanceList.size() > 0){
	    		ServiceInstance<ThriftServicePayload> service = instanceList.iterator().next();
	    		System.out.println(service);
	    		
	    		if(service != null){
					System.out.println(service.getName());
					System.out.println(service.getId());
					System.out.println(service.getAddress());
					System.out.println(service.getPort());
					System.out.println(service.getSslPort());
					System.out.println(service.getPayload());
					System.out.println(service.getRegistrationTimeUTC());
					System.out.println(service.getServiceType());
					System.out.println(service.getUriSpec());
		   	 	}
	    	}
	    }
		
		serviceRegistry.close();
//		stopTestingServer();
	}
	
}
