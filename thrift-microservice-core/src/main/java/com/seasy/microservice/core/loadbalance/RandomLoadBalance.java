package com.seasy.microservice.core.loadbalance;

import java.util.Collection;
import java.util.Random;

import org.apache.curator.x.discovery.ServiceInstance;

import com.seasy.microservice.core.common.ThriftServicePayload;

/**
 * 随机
 */
public class RandomLoadBalance extends AbstractLoadBalance {
    public static final String NAME = "random";
    private final Random random = new Random();
    
    @Override
    protected ServiceInstance<ThriftServicePayload> doSelect(Collection<ServiceInstance<ThriftServicePayload>> instances, String serviceName) {
    	int length = instances.size();
		Object[] arr = new Object[length];
		instances.toArray(arr);
		return (ServiceInstance<ThriftServicePayload>)arr[random.nextInt(length)];
    }
    
}
