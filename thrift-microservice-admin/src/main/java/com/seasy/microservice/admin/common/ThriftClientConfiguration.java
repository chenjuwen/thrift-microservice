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
	
	@Value("${thrift.consumer.register}")
    private String consumerRegister;
	
	@Bean(initMethod="start", destroyMethod="stop")
	public ClientBootstrap getClientBootstrap(){
		ClientBootstrap clientBootstrap = new ThriftClientBootstrap(registryAddress);
		
		if("true".equalsIgnoreCase(consumerRegister)){
			clientBootstrap.setRegister(true);
		}else{
			clientBootstrap.setRegister(false);
		}
		
		return clientBootstrap;
	}
	
}
