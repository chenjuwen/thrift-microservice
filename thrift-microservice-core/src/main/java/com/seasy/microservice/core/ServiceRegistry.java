package com.seasy.microservice.core;

import java.util.Collection;

import org.apache.curator.x.discovery.ServiceInstance;

import com.seasy.microservice.core.common.CuratorHelper;
import com.seasy.microservice.core.common.ThriftServicePayload;

public interface ServiceRegistry {
	public static final String ZNODE_PATH_SERVICE = "/service";
	void start() throws Exception;
	void close();
	
	void registerService(ServiceInstance<ThriftServicePayload> instance) throws Exception;
	void unregisterService(ServiceInstance<ThriftServicePayload> instance) throws Exception;
	void updateService(ServiceInstance<ThriftServicePayload> instance) throws Exception;
	
	Collection<ServiceInstance<ThriftServicePayload>> queryForInstances(String name) throws Exception;
	ServiceInstance<ThriftServicePayload> queryForInstance(String name, String id) throws Exception;
	Collection<String> queryForNames() throws Exception;
	
	CuratorHelper getCuratorHelper();
}
