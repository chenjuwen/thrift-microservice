package com.seasy.microservice.core;

import com.seasy.microservice.core.loadbalance.LoadBalance;

public interface ClientBootstrap extends Bootstrap{
	<T> T getServiceClient(Class<T> serviceClientClass);
	Object getServiceClient(String serviceName);
	ServiceRegistry getServiceRegistry();
	void setLoadBalance(LoadBalance loadBalance);
}