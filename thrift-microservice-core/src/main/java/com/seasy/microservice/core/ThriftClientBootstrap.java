package com.seasy.microservice.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;

import com.seasy.microservice.core.common.LoggerFactory;
import com.seasy.microservice.core.common.ServiceClientFactory;
import com.seasy.microservice.core.common.ServiceClientWrapper;
import com.seasy.microservice.core.common.ThriftServicePayload;
import com.seasy.microservice.core.loadbalance.LoadBalance;
import com.seasy.microservice.core.loadbalance.RandomLoadBalance;
import com.seasy.microservice.core.utils.EnvUtil;

public class ThriftClientBootstrap extends AbstractBootstrap implements ClientBootstrap{
	private static final Logger logger = LoggerFactory.getLogger(ThriftClientBootstrap.class);
	
	//key=host:port, value=ServiceClientFactory
	private ConcurrentHashMap<String, ServiceClientFactory> serviceClientFactoryMap = new ConcurrentHashMap<>();
	private ConsumerHelper consumerHelper;
	private String localIp;
	private LoadBalance loadBalance;
	
	public ThriftClientBootstrap(String registryAddress){
		super(registryAddress);
		this.localIp = EnvUtil.getLocalIp();
	}
	
	@Override
	public void start() throws Exception {
		logger.debug("start ThriftClientBootstrap...");
		super.start();
		
		loadBalance = new RandomLoadBalance(); //负载均衡：随机
		
		initConsumerHelper();
	}
	
	/**
	 * 初始化根znode
	 */
	private void initConsumerHelper()throws Exception{
		consumerHelper = new ConsumerHelper(serviceRegistry.getCuratorHelper());
		consumerHelper.initRootZnode();
		
		//注册消费者
		if(isRegister()){
			consumerHelper.register(this.localIp);
		}
	}
	
	@Override
	public void stop(){
		logger.debug("stop ThriftClientBootstrap...");
		
		//反注册消费者
		if(consumerHelper != null && isRegister()){
			consumerHelper.unregister(this.localIp);
		}
		
		super.stop();
		
		//关闭所有ServiceClientFactory
		for(Iterator<ServiceClientFactory> it=serviceClientFactoryMap.values().iterator(); it.hasNext(); ){
			it.next().destroy();
		}
		serviceClientFactoryMap.clear();
	}

	/**
	 * 获取ServiceClient实例对象
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getServiceClient(Class<T> serviceClientClass){
		try{
			String serviceClientClassName = serviceClientClass.getName();
			if(!serviceClientClassName.endsWith("$Client")){
				throw new IllegalArgumentException("serviceClientClass must be $Client class");
			}
			
			String serviceName = serviceClientClassName.replace("$Client", "");
			serviceName = serviceName.substring(serviceName.lastIndexOf(".")+1);
			
			Object object = getServiceClient(serviceName);
			if(object != null){
				return (T)object;
			}
			
		}catch(Exception ex){
			logger.error("Failed to get ServiceClient", ex);
		}
		return null;
	}
	
	@Override
	public Object getServiceClient(String serviceName) {
		try{
			ServiceInstance<ThriftServicePayload> serviceInstance = queryForInstance(serviceName);
			if(serviceInstance != null){
				//服务所在机器的IP地址
				String host = serviceInstance.getAddress();
				
				//服务所在机器的监听端口
				int port = serviceInstance.getPort();
				
				ServiceClientFactory factory = null;
				ServiceClientWrapper wrapper = null;
				
				//根据服务器地址和端口获取一个ServiceClientFactory对象
				String key = host + ":" + port;
				if(serviceClientFactoryMap.containsKey(key)){
					factory = serviceClientFactoryMap.get(key);
					wrapper = factory.getServiceClientWrapper(serviceName);
				}else{
					logger.info("create ServiceClientFactory...");
					factory = ServiceClientFactory.getInstance();
					factory.setHost(host);
					factory.setPort(port);
					factory.open();
					
					serviceClientFactoryMap.put(key, factory);
				}
				
				if(wrapper == null){
					Class<?> serviceClientClass = Class.forName(serviceInstance.getPayload().getInterfaceName() + "$Client");
					
					wrapper = new ServiceClientWrapper(serviceInstance, serviceClientClass, serviceName);
					factory.addServiceClientWrapper(wrapper);
					
					return factory.getServiceClientWrapper(serviceName).getServiceClientInstanceObject();
				}else{
					return wrapper.getServiceClientInstanceObject();
				}
			}else{
				//服务不存在
				logger.error("service not found: " + serviceName);
			}
			
		}catch(Exception ex){
			logger.error("Failed to get ServiceClient", ex);
		}
		return null;
	}
	
	/**
	 * 到注册中心查找指定名称的服务信息
	 * @param serviceName 服务名
	 */
	private ServiceInstance<ThriftServicePayload> queryForInstance(String serviceName)throws Exception{
		Collection<ServiceInstance<ThriftServicePayload>> instanceList = serviceRegistry.queryForInstances(serviceName);
		
		//负载均衡
		ServiceInstance<ThriftServicePayload> instance = loadBalance.select(instanceList, serviceName);
		if(instance != null){
			logger.debug("服务地址： " + instance.getAddress() + ":" + instance.getPort());
		}
		return instance;
	}
	
	@Override
	public ServiceRegistry getServiceRegistry() {
		return this.serviceRegistry;
	}
	
}
