package com.seasy.microservice.core.loadbalance;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.curator.x.discovery.ServiceInstance;

import com.seasy.microservice.core.common.ThriftServicePayload;

/**
 * 粘性
 */
public class StickyLoadBalance extends AbstractLoadBalance {
    public static final String NAME = "sticky";
    private LoadBalance masterLoadBalance;
    private AtomicReference<ServiceInstance<ThriftServicePayload>> reference = new AtomicReference<>();
    
    public StickyLoadBalance(){
    	this(new RandomLoadBalance());
    }
    
    public StickyLoadBalance(LoadBalance masterLoadBalance){
    	this.masterLoadBalance = masterLoadBalance;
    }
    
    @Override
    protected ServiceInstance<ThriftServicePayload> doSelect(Collection<ServiceInstance<ThriftServicePayload>> instances, String serviceName) {
    	ServiceInstance<ThriftServicePayload> localServiceInstance = reference.get();
    	if(!instances.contains(localServiceInstance)){
    		reference.compareAndSet(localServiceInstance, null);
    	}
    	
    	if(reference.get() == null){
    		ServiceInstance<ThriftServicePayload> instance = masterLoadBalance.select(instances, serviceName);
    		reference.compareAndSet(null, instance);
    	}
    	
    	return reference.get();
    }
    
}
