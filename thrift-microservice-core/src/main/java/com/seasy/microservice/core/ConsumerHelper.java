package com.seasy.microservice.core;

import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;

import com.seasy.microservice.core.common.CuratorHelper;
import com.seasy.microservice.core.common.DataKeys;
import com.seasy.microservice.core.common.LoggerFactory;
import com.seasy.microservice.core.utils.DatetimeUtil;

import net.sf.json.JSONObject;

/**
 * 服务消费者
 */
public class ConsumerHelper {
	private static final Logger logger = LoggerFactory.getLogger(ConsumerHelper.class);
	public static final String ZNODE_PATH_CONSUMER = "/consumer";
	private CuratorHelper curatorHelper;
	
	public ConsumerHelper(CuratorHelper curatorHelper){
		this.curatorHelper = curatorHelper;
	}
	
	public void initRootZnode(){
		curatorHelper.createZnode(ConsumerHelper.ZNODE_PATH_CONSUMER, CreateMode.PERSISTENT);
	}
	
	/**
	 * 注册到注册中心
	 * @param address 提供者地址
	 * @param port 提供者端口
	 */
	public void register(String address){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(DataKeys.Consumer.IP.name(), address);
		jsonObject.put(DataKeys.Consumer.TIME.name(), DatetimeUtil.getToday(DatetimeUtil.DEFAULT_PATTERN_DT));
		
		String data = jsonObject.toString();
		String znodePath = ConsumerHelper.ZNODE_PATH_CONSUMER + "/" + address;
		curatorHelper.createZnode(znodePath, CreateMode.EPHEMERAL, data.getBytes());
		logger.info("register consumer: " + znodePath);
	}
	
}
