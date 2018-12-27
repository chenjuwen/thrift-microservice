package com.seasy.microservice.core;

import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;

import com.seasy.microservice.core.common.CuratorHelper;
import com.seasy.microservice.core.common.DataKeys;
import com.seasy.microservice.core.common.LoggerFactory;
import com.seasy.microservice.core.utils.DatetimeUtil;

import net.sf.json.JSONObject;

/**
 * 服务提供者
 */
public class ProviderHelper {
	private static final Logger logger = LoggerFactory.getLogger(ProviderHelper.class);
	public static final String ZNODE_PATH_PROVIDER = "/provider";
	private CuratorHelper curatorHelper;
	
	public ProviderHelper(CuratorHelper curatorHelper){
		this.curatorHelper = curatorHelper;
	}
	
	public void initRootZnode(){
		curatorHelper.createZnode(ProviderHelper.ZNODE_PATH_PROVIDER, CreateMode.PERSISTENT);
	}
	
	/**
	 * 注册到注册中心
	 * @param address 提供者地址
	 * @param port 提供者端口
	 */
	public void register(String address, String port){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(DataKeys.Provider.IP.name(), address);
		jsonObject.put(DataKeys.Provider.PORT.name(), port);
		jsonObject.put(DataKeys.Provider.TIME.name(), DatetimeUtil.getToday(DatetimeUtil.DEFAULT_PATTERN_DT));
		
		String data = jsonObject.toString();
		String znodePath = ProviderHelper.ZNODE_PATH_PROVIDER + "/" + address + ":" + port;
		curatorHelper.createZnode(znodePath, CreateMode.EPHEMERAL, data.getBytes());
		logger.info("register provider: " + znodePath);
	}
	
}
