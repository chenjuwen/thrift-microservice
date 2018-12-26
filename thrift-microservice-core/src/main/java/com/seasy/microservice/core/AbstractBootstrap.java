package com.seasy.microservice.core;

import org.slf4j.Logger;

public abstract class AbstractBootstrap implements Bootstrap {
	private static final Logger logger = LoggerFactory.getLogger(AbstractBootstrap.class);
	protected String registryAddress; //服务注册中心地址
	protected ServiceRegistry serviceRegistry;
	
	@Override
	public void start() throws Exception {
		//启动服务注册管理器
		serviceRegistry = new ThriftServiceRegistry(registryAddress, ServiceRegistry.ZNODE_PATH_SERVICE);
		serviceRegistry.start();
		logger.info("ServiceRegistry started!");
	}

	@Override
	public void stop() {
		//服务注册管理器
		if(serviceRegistry != null){
			serviceRegistry.close();
			serviceRegistry = null;
		}
	}

}
