package com.seasy.microservice.core;

import java.util.Collection;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;

import com.seasy.microservice.core.common.ThriftServicePayload;

/**
 * thrift服务注册类
 */
public class ThriftServiceRegistry implements ServiceRegistry{
	private static final Logger logger = LoggerFactory.getLogger(ThriftServiceRegistry.class);	
	private CuratorFramework curator;
	private ServiceDiscovery<ThriftServicePayload> serviceDiscovery;
	
	private String connectString; //zookeeper连接字符串
	private String basePath; //zookeeper中存储服务的根路径
	
	private boolean enableCache = false;
	private TreeCache treeCache = null;
	
	public ThriftServiceRegistry(String connectString, String basePath){
		this.connectString = connectString;
        this.basePath = basePath;
    }

    @Override
	public void start() throws Exception {
        startCurator();
        startServiceDiscovery();
    }
	
	/**
	 * 要以单例模式创建CuratorFramework对象
	 */
	private void startCurator()throws Exception{
		//构造CuratorFramework实例
		curator = CuratorFrameworkFactory.builder()
				.connectString(connectString)
				.sessionTimeoutMs(60 * 1000)
				.connectionTimeoutMs(10 * 1000)
				.retryPolicy(new RetryNTimes(3, 3000))
				.namespace("thrift-microservice")
				.build();
		
		//连接监听器
		curator.getConnectionStateListenable().addListener(new ConnectionStateListener() {
			@Override
			public void stateChanged(CuratorFramework client, ConnectionState newState) {
				if (newState == ConnectionState.LOST) {
                    System.out.println("连接丢失");
                } else if (newState == ConnectionState.CONNECTED) {
                    System.out.println("连接新建"); 
                } else if (newState == ConnectionState.RECONNECTED) {
                    System.out.println("连接重连");
                } else if (newState == ConnectionState.SUSPENDED) {
                	System.out.println("连接暂停");
                } 
			}
		});
		
		//启动curator
		curator.start();
		
		if(enableCache){
			//节点操作监听
			treeCache = new TreeCache(curator, ServiceRegistry.ZNODE_PATH_SERVICE);
			treeCache.start();
			
			treeCache.getListenable().addListener(new TreeCacheListener() {
				@Override
				public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
					System.out.println("type=" + event.getType().name());
					if(event.getData() != null){
						System.out.println("路径：" + event.getData().getPath());
						System.out.println("数据：" + new String(event.getData().getData()));
					}
				}
			});
		}		
	}
	
	private void startServiceDiscovery()throws Exception{
		//构造ServiceDiscovery实例
        serviceDiscovery = ServiceDiscoveryBuilder.builder(ThriftServicePayload.class)
            .client(curator)
            .serializer(new JsonInstanceSerializer<>(ThriftServicePayload.class))
            .basePath(basePath)
            .build();
        
        //启动ServiceDiscovery
        serviceDiscovery.start();
	}
	
	@Override
	public void createZnode(String path, CreateMode mode) {
		try{
			if(curator.checkExists().forPath(path) == null){
				curator.create().creatingParentsIfNeeded().withMode(mode).forPath(path);
			}
		}catch(Exception ex){
			logger.error("create znode error", ex);
		}
	}
	
	@Override
	public void createZnode(String path, CreateMode mode, byte[] data) {
		try{
			if(curator.checkExists().forPath(path) == null){
				curator.create().creatingParentsIfNeeded().withMode(mode).forPath(path, data);
				logger.debug("create znode: " + path);
			}
		}catch(Exception ex){
			logger.error("create znode error", ex);
		}
	}
	
	@Override
	public List<String> getChildren(String path) {
		try{
			return curator.getChildren().forPath(path);
		}catch(Exception ex){
			logger.error("get children error", ex);
			return null;
		}
	}
	
	@Override
	public byte[] getData(String path) {
		try{
			return curator.getData().forPath(path);
		}catch(Exception ex){
			logger.error("get data error", ex);
			return null;
		}
	}

    @Override
	public void close(){
    	if(treeCache != null){
    		treeCache.close();
    		treeCache = null;
    	}
    	
    	CloseableUtils.closeQuietly(serviceDiscovery);
    	CloseableUtils.closeQuietly(curator);
    }

	/**
	 * 注册服务
	 */
    @Override
	public void registerService(ServiceInstance<ThriftServicePayload> instance) throws Exception {
        serviceDiscovery.registerService(instance);
    }

	/**
	 * 注销服务
	 */
    @Override
	public void unregisterService(ServiceInstance<ThriftServicePayload> instance) throws Exception {
        serviceDiscovery.unregisterService(instance);
    }

	/**
	 * 更新服务
	 */
    @Override
	public void updateService(ServiceInstance<ThriftServicePayload> instance) throws Exception {
        serviceDiscovery.updateService(instance);
    }

	/**
	 * 查询服务
	 */
    @Override
	public Collection<ServiceInstance<ThriftServicePayload>> queryForInstances(String name) throws Exception {
        return serviceDiscovery.queryForInstances(name);
    }

	/**
	 * 查询服务
	 */
    @Override
	public ServiceInstance<ThriftServicePayload> queryForInstance(String name, String id) throws Exception {
        return serviceDiscovery.queryForInstance(name, id);
    }

	/**
	 * 更新服务名
	 */
    @Override
	public Collection<String> queryForNames() throws Exception {
    	return serviceDiscovery.queryForNames();
    }
	
}
