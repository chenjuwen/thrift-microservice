package com.seasy.microservice.core.loadbalance;

import java.util.Collection;

import org.apache.curator.x.discovery.ServiceInstance;

import com.seasy.microservice.core.common.ThriftServicePayload;

public interface LoadBalance {
	ServiceInstance<ThriftServicePayload> select(Collection<ServiceInstance<ThriftServicePayload>> instances, String serviceName);
}
