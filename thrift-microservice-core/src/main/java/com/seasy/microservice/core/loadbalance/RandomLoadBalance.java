package com.seasy.microservice.core.loadbalance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.apache.curator.shaded.com.google.common.collect.Lists;
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
    	ArrayList<ServiceInstance<ThriftServicePayload>> list = Lists.newArrayList(instances);
    	int length = instances.size();
    	return list.get(random.nextInt(length));
    }
    
}
