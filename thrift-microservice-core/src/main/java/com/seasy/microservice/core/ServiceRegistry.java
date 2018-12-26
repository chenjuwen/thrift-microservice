package com.seasy.microservice.core;

import java.util.Collection;
import java.util.List;

import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.zookeeper.CreateMode;

import com.seasy.microservice.core.common.ThriftServicePayload;

public interface ServiceRegistry {
	public static final String ZNODE_PATH_SERVICE = "/service";
	public static final String ZNODE_PATH_PROVIDER = "/provider";
	public static final String ZNODE_PATH_CONSUMER = "/consumer";
	
	void start() throws Exception;
	void close();
	
	void createZnode(String path, CreateMode mode);
	void createZnode(String path, CreateMode mode, byte[] data);
	List<String> getChildren(String path);
	byte[] getData(String path);
	
	void registerService(ServiceInstance<ThriftServicePayload> instance) throws Exception;
	void unregisterService(ServiceInstance<ThriftServicePayload> instance) throws Exception;
	void updateService(ServiceInstance<ThriftServicePayload> instance) throws Exception;
	
	Collection<ServiceInstance<ThriftServicePayload>> queryForInstances(String name) throws Exception;
	ServiceInstance<ThriftServicePayload> queryForInstance(String name, String id) throws Exception;
	Collection<String> queryForNames() throws Exception;
}
