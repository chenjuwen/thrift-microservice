package com.seasy.microservice.core.loadbalance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.curator.shaded.com.google.common.collect.Lists;
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
            sequences.putIfAbsent(serviceName, new AtomicInteger(0));
            sequence = sequences.get(serviceName);
        }
        
        int currentSequence = sequence.getAndIncrement();
        
        ArrayList<ServiceInstance<ThriftServicePayload>> list = Lists.newArrayList(instances);
    	int length = instances.size();
    	return list.get(currentSequence % length);
    }
    
}
