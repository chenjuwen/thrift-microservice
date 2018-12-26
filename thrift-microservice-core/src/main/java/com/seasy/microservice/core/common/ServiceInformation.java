package com.seasy.microservice.core.common;

/**
 * 服务注解信息
 */
public class ServiceInformation {
	private Object serviceImplementClassInstance;
	
	private String id;
	private Class<?> serviceClass;
	private String version;
	private int timeout = 0;
	
	private boolean processorRegistered = false; //是否发布到thrift处理器
	private boolean serviceRegistered = false; //是否注册到注册中心
	
	public Object getServiceImplementClassInstance() {
		return serviceImplementClassInstance;
	}

	public void setServiceImplementClassInstance(Object serviceImplementClassInstance) {
		this.serviceImplementClassInstance = serviceImplementClassInstance;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Class<?> getServiceClass() {
		return serviceClass;
	}
	
	public void setServiceClass(Class<?> serviceClass) {
		this.serviceClass = serviceClass;
	}

	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public int getTimeout() {
		return timeout;
	}
	
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public boolean isProcessorRegistered() {
		return processorRegistered;
	}

	public void setProcessorRegistered(boolean processorRegistered) {
		this.processorRegistered = processorRegistered;
	}

	public boolean isServiceRegistered() {
		return serviceRegistered;
	}

	public void setServiceRegistered(boolean serviceRegistered) {
		this.serviceRegistered = serviceRegistered;
	}
	
}
