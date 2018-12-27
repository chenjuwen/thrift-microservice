package com.seasy.microservice.core;

import java.util.Collection;

import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import com.seasy.microservice.core.common.CuratorHelper;
import com.seasy.microservice.core.common.ThriftServicePayload;

/**
 * thrift服务注册类
 */
public class ThriftServiceRegistry implements ServiceRegistry{	
	private CuratorHelper curatorHelper;
	private ServiceDiscovery<ThriftServicePayload> serviceDiscovery;
	private String basePath; //zookeeper中存储服务的根路径
	
	public ThriftServiceRegistry(String connectString, String basePath){
        this.basePath = basePath;
        curatorHelper = new CuratorHelper(connectString);
    }

    @Override
	public void start() throws Exception {
    	initCuratorHelper();
        initServiceDiscovery();
    }
	
	private void initCuratorHelper()throws Exception{
		curatorHelper.start();
	}
	
	private void initServiceDiscovery()throws Exception{
		//构造ServiceDiscovery实例
        serviceDiscovery = ServiceDiscoveryBuilder.builder(ThriftServicePayload.class)
            .client(curatorHelper.getCurator())
            .serializer(new JsonInstanceSerializer<>(ThriftServicePayload.class))
            .basePath(basePath)
            .build();
        
        //启动ServiceDiscovery
        serviceDiscovery.start();
	}
	
    @Override
	public void close(){
    	CloseableUtils.closeQuietly(serviceDiscovery);
    	curatorHelper.close();
    }

	/**
	 * 注册服务
	 */
    @Override
	public void registerService(ServiceInstance<ThriftServicePayload> instance) throws Exception {
        serviceDiscovery.registerService(instance);
    }

	/**
	 * 注销服务
	 */
    @Override
	public void unregisterService(ServiceInstance<ThriftServicePayload> instance) throws Exception {
        serviceDiscovery.unregisterService(instance);
    }

	/**
	 * 更新服务
	 */
    @Override
	public void updateService(ServiceInstance<ThriftServicePayload> instance) throws Exception {
        serviceDiscovery.updateService(instance);
    }

	/**
	 * 查询服务
	 */
    @Override
	public Collection<ServiceInstance<ThriftServicePayload>> queryForInstances(String name) throws Exception {
        return serviceDiscovery.queryForInstances(name);
    }

	/**
	 * 查询服务
	 */
    @Override
	public ServiceInstance<ThriftServicePayload> queryForInstance(String name, String id) throws Exception {
        return serviceDiscovery.queryForInstance(name, id);
    }

	/**
	 * 更新服务名
	 */
    @Override
	public Collection<String> queryForNames() throws Exception {
    	return serviceDiscovery.queryForNames();
    }

    @Override
	public CuratorHelper getCuratorHelper() {
		return curatorHelper;
	}
	
}
