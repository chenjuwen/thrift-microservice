package com.seasy.microservice.core;

public interface ClientBootstrap extends Bootstrap{
	<T> T getServiceClient(Class<T> serviceClientClass);
	Object getServiceClient(String serviceName);
	ServiceRegistry getServiceRegistry();
}