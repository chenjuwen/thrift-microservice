package com.seasy.microservice.core.loadbalance;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.curator.x.discovery.ServiceInstance;

import com.seasy.microservice.core.common.ThriftServicePayload;

/**
 * 轮询
 */
public class RoundRobinLoadBalance extends AbstractLoadBalance {
    public static final String NAME = "roundrobin";
    private ConcurrentMap<String, AtomicInteger> sequences = new ConcurrentHashMap<String, AtomicInteger>();
    
    @Override
    protected ServiceInstance<ThriftServicePayload> doSelect(Collection<ServiceInstance<ThriftServicePayload>> instances, String serviceName) {
    	AtomicInteger sequence = sequences.get(serviceName);
        if (sequence == null) {
            sequences.putIfAbsent(serviceName, new AtomicInteger());
            sequence = sequences.get(serviceName);
        }
        
        int currentSequence = sequence.getAndIncrement();
        
        int length = instances.size();
		Object[] arr = new Object[length];
		instances.toArray(arr);
		return (ServiceInstance<ThriftServicePayload>)arr[currentSequence % length];
    }
    
}
