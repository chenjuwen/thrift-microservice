package com.seasy.microservice.core.loadbalance;

import java.util.Collection;

import org.apache.curator.x.discovery.ServiceInstance;

import com.seasy.microservice.core.common.ThriftServicePayload;

public abstract class AbstractLoadBalance implements LoadBalance{
	@Override
	public ServiceInstance<ThriftServicePayload> select(Collection<ServiceInstance<ThriftServicePayload>> instances, String serviceName) {
		if(instances == null || instances.isEmpty()){
			return null;
		}else if(instances.size() == 1){
			return instances.iterator().next();
		}else{
			return doSelect(instances, serviceName);
		}
	}
	
	protected abstract ServiceInstance<ThriftServicePayload> doSelect(Collection<ServiceInstance<ThriftServicePayload>> instances, String serviceName);
}
