package com.seasy.microservice.core;

import org.slf4j.Logger;

import com.seasy.microservice.core.common.LoggerFactory;

public abstract class AbstractBootstrap implements Bootstrap {
	private static final Logger logger = LoggerFactory.getLogger(AbstractBootstrap.class);
	protected ServiceRegistry serviceRegistry;
	private boolean register = true; //是否注册到注册中心
	
	/**
	 * @param registryAddress 服务注册中心地址
	 */
	public AbstractBootstrap(String registryAddress){
		serviceRegistry = new ThriftServiceRegistry(registryAddress, ServiceRegistry.ZNODE_PATH_SERVICE);
	}
	
	@Override
	public void start() throws Exception {
		//启动服务注册管理器
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

	@Override
	public boolean isRegister() {
		return register;
	}

	@Override
	public void setRegister(boolean register) {
		this.register = register;
	}

}
