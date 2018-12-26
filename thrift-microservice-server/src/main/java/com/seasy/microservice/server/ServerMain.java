package com.seasy.microservice.server;

import com.seasy.microservice.core.ServerBotstrap;
import com.seasy.microservice.core.ThriftServerBootstrap;

public class ServerMain {
	public static void main(String[] args) {
		try{
			int port = 20001;
			String serviceBasePackage = "com.seasy.microservice.server";
			String registryAddress = "192.168.134.134:2181";
			
			ServerBotstrap serverBootstrap = new ThriftServerBootstrap(port, serviceBasePackage, registryAddress);
			serverBootstrap.start();
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
