package com.seasy.microservice.core.common;

import org.apache.curator.x.discovery.ServiceInstance;

/**
 * ServiceClient包装类
 */
public class ServiceClientWrapper {
	private ServiceInstance<ThriftServicePayload> serviceInstance;
    private Class<?> serviceClientClass;
    private String serviceName;
	private Object serviceClientInstanceObject; //服务客户端类的实例对象
    
    public ServiceClientWrapper(ServiceInstance<ThriftServicePayload> serviceInstance, Class<?> serviceClientClass, String serviceName){
    	this.serviceInstance = serviceInstance;
    	this.serviceClientClass = serviceClientClass;
    	this.serviceName = serviceName;
    }

	public ServiceInstance<ThriftServicePayload> getServiceInstance() {
		return serviceInstance;
	}

	public Class<?> getServiceClientClass() {
		return serviceClientClass;
	}

	public String getServiceName() {
		return serviceName;
	}
	
	public Object getServiceClientInstanceObject() {
		return serviceClientInstanceObject;
	}

	public void setServiceClientInstanceObject(Object serviceClientInstanceObject) {
		this.serviceClientInstanceObject = serviceClientInstanceObject;
	}
}
