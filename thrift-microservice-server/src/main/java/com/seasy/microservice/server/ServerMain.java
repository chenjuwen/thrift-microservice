package com.seasy.microservice.server;

import com.seasy.microservice.core.ServerBotstrap;
import com.seasy.microservice.core.ThriftServerBootstrap;

public class ServerMain {
	public static void main(String[] args) {
		ServerBotstrap serverBootstrap = null;
		try{
			int port = 20001; //监听端口
			String serviceBasePackage = "com.seasy.microservice.server"; //业务实现类所在包路径
			String registryAddress = "192.168.134.134:2181"; //ZK注册中心地址
			
			serverBootstrap = new ThriftServerBootstrap(port, serviceBasePackage, registryAddress);
			serverBootstrap.start();
			
		}catch(Exception ex){
			ex.printStackTrace();
			
			if(serverBootstrap != null){
				serverBootstrap.stop();
			}
		}
	}
}
