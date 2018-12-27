package com.seasy.microservice.core.common;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;

import com.seasy.microservice.core.utils.StringUtil;

/**
 * ServiceClient工厂类：
 *  	一个ServiceClientFactory代表一个服务提供者，可能提供多个服务
 *  	服务Client对象存放在serviceClientWrapperMap集合中
 */
public class ServiceClientFactory {
	private static final Logger logger = LoggerFactory.getLogger(ServiceClientFactory.class);
	private static ServiceClientFactory factory = new ServiceClientFactory();
    private TTransport transport = null;
	private String host = null;
	private int port = 0;
	
	//key=serviceName, value=ServiceClientWrapper
	private ConcurrentHashMap<String, ServiceClientWrapper> serviceClientWrapperMap = new ConcurrentHashMap<>();
    
	private ServiceClientFactory(){
		
	}
	
	public static ServiceClientFactory getInstance(){
		return factory;
	}
    
    public void open(){
    	try{
    		if(StringUtil.isNotEmpty(this.host) && this.port != 0){
    			if(transport == null){
    				transport = new TFramedTransport(new TSocket(this.host, this.port));
    				transport.open();
    				logger.debug("Transport opened --> " + this.host + ":" + this.port);
    			}else if(!transport.isOpen()){
    				transport.open();
    			}
    		}else{
				throw new IllegalArgumentException("parameter host or port is invalid!");
    		}
		}catch(Exception ex){
			close();
			throw new RuntimeException("Failed to open service Socket", ex);
		}
    }
    
    private void close(){
    	if(transport != null){
    		transport.close();
    	}
    	serviceClientWrapperMap.clear();
    }
    
    public void destroy(){
    	close();
    	this.transport = null;
    	this.host = null;
    	this.port = 0;
    }
	
	public ServiceClientWrapper getServiceClientWrapper(String serviceName){
		return serviceClientWrapperMap.get(serviceName);
	}
	
	public void addServiceClientWrapper(ServiceClientWrapper wrapper){
		if(!serviceClientWrapperMap.containsKey(wrapper.getServiceName())){
			wrapper = instanceServiceClient(wrapper);
			serviceClientWrapperMap.put(wrapper.getServiceName(), wrapper);
		}
	}
	
	/**
	 * 实例化ServiceClient
	 */
	private ServiceClientWrapper instanceServiceClient(ServiceClientWrapper wrapper){
		try{
			logger.debug("instance ServiceClient: " + wrapper.getServiceClientClass().getName());
			TProtocol protocol = new TCompactProtocol(transport);
			TMultiplexedProtocol multiplexedProtocol = new TMultiplexedProtocol(protocol, wrapper.getServiceName());
	        Class[] classes = new Class[]{TProtocol.class};
	        Object serviceClientInstanceObject = wrapper.getServiceClientClass().getConstructor(classes).newInstance(multiplexedProtocol);
	        wrapper.setServiceClientInstanceObject(serviceClientInstanceObject);
	        return wrapper;
		}catch(Exception ex){
			logger.error("instance ServiceClient error", ex);
		}
		return wrapper;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
}
