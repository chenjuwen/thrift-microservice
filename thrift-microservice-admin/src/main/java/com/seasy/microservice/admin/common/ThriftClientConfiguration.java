package com.seasy.microservice.admin.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.seasy.microservice.core.ClientBootstrap;
import com.seasy.microservice.core.ThriftClientBootstrap;

@Configuration
public class ThriftClientConfiguration {
	@Value("${thrift.registry.address}")
    private String registryAddress;
	
	@Bean(initMethod="start", destroyMethod="stop")
	public ClientBootstrap getClientBootstrap(){
		ClientBootstrap clientBootstrap = new ThriftClientBootstrap(registryAddress);
		return clientBootstrap;
	}
	
}
