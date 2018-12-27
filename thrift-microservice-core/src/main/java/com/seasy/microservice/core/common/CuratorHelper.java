package com.seasy.microservice.core.common;

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
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;

import com.seasy.microservice.core.ServiceRegistry;

public class CuratorHelper {
	private static final Logger logger = LoggerFactory.getLogger(CuratorHelper.class);	
	private CuratorFramework curator;
	private String connectString; //zookeeper连接字符串

	private boolean enableCache = false;
	private TreeCache treeCache = null;
	
	public CuratorHelper(String connectString){
		this.connectString = connectString;
	}

	public void start() throws Exception{
		//以单例模式创建CuratorFramework对象
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
	
	public void close(){
    	if(treeCache != null){
    		treeCache.close();
    		treeCache = null;
    	}
    	
    	CloseableUtils.closeQuietly(curator);
	}
	
	public boolean exists(String path){
		try {
			return curator.checkExists().forPath(path) != null;
		} catch (Exception ex) {
			logger.error("check exists error: " + ex.toString());
		}
		return false;
	}
	
	public void createZnode(String path, CreateMode mode){
		createZnode(path, mode, new byte[0]);
	}
	
	public void createZnode(String path, CreateMode mode, byte[] data){
		try{
			if(!exists(path)){
				curator.create().creatingParentsIfNeeded().withMode(mode).forPath(path, data);
			}
		}catch(Exception ex){
			logger.error("create znode error: " + ex.toString());
		}
	}
	
	public List<String> getChildren(String path){
		try{
			return curator.getChildren().forPath(path);
		}catch(Exception ex){
			logger.error("get children error: " + ex.toString());
			return null;
		}
	}
	
	public byte[] getData(String path){
		try{
			return curator.getData().forPath(path);
		}catch(Exception ex){
			logger.error("get data error: " + ex.toString());
			return null;
		}
	}
	
	public void delete(String path){
		try{
			if(exists(path)){
				curator.delete().guaranteed().deletingChildrenIfNeeded().forPath(path);
			}
		}catch(Exception ex){
			logger.error("delete path error: " + ex.toString());
		}
	}

	public CuratorFramework getCurator() {
		return curator;
	}
	
}
