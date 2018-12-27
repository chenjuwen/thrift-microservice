package com.seasy.microservice.admin.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.seasy.microservice.admin.bean.ConsumerBean;
import com.seasy.microservice.admin.bean.ProviderBean;
import com.seasy.microservice.admin.bean.ServiceBean;
import com.seasy.microservice.core.ClientBootstrap;
import com.seasy.microservice.core.ConsumerHelper;
import com.seasy.microservice.core.ProviderHelper;
import com.seasy.microservice.core.common.DataKeys;
import com.seasy.microservice.core.common.ThriftServicePayload;
import com.seasy.microservice.core.utils.DatetimeUtil;
import com.seasy.microservice.core.utils.JsonUtil;

import net.sf.json.JSONObject;

@Service
public class ServiceDiscoveryService {
	@Autowired
	private ClientBootstrap clientBootstrap;
	
	/**
	 * 服务提供者
	 */
	public Map<String, ProviderBean> getProviderList(){
		Map<String, ProviderBean> beanMap = new HashMap<>();
		
		try{
			List<String> pathList = clientBootstrap.getServiceRegistry().getCuratorHelper().getChildren(ProviderHelper.ZNODE_PATH_PROVIDER);
			for(String path : pathList){
				String subpath = ProviderHelper.ZNODE_PATH_PROVIDER + "/" + path;
				byte[] data = clientBootstrap.getServiceRegistry().getCuratorHelper().getData(subpath);
				JSONObject jsonObject = JsonUtil.string2object(new String(data));
				
				ProviderBean bean = new ProviderBean();
				bean.setAddress(jsonObject.getString(DataKeys.Provider.IP.name()));
				bean.setPort(jsonObject.getString(DataKeys.Provider.PORT.name()));
				bean.setTime(jsonObject.getString(DataKeys.Provider.TIME.name()));
				
				String key = bean.getAddress() + ":" + bean.getPort();
				beanMap.put(key, bean);
		    }
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return beanMap;
	}
	
	/**
	 * 服务消费者
	 */
	public Map<String, ConsumerBean> getConsumerList(){
		Map<String, ConsumerBean> beanMap = new HashMap<>();
		
		try{
			List<String> pathList = clientBootstrap.getServiceRegistry().getCuratorHelper().getChildren(ConsumerHelper.ZNODE_PATH_CONSUMER);
			for(String path : pathList){
				String subpath = ConsumerHelper.ZNODE_PATH_CONSUMER + "/" + path;
				byte[] data = clientBootstrap.getServiceRegistry().getCuratorHelper().getData(subpath);
				JSONObject jsonObject = JsonUtil.string2object(new String(data));
				
				ConsumerBean bean = new ConsumerBean();
				bean.setAddress(jsonObject.getString(DataKeys.Consumer.IP.name()));
				bean.setTime(jsonObject.getString(DataKeys.Consumer.TIME.name()));
				
				beanMap.put(bean.getAddress(), bean);
		    }
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return beanMap;
	}

	/**
	 * 业务服务
	 */
	public Map<String, ServiceBean> getServiceList(){
		Map<String, ServiceBean> beanMap = new HashMap<>();
		
		try{
			//获取所有服务名
			Collection<String> nameList = clientBootstrap.getServiceRegistry().queryForNames();
			for(String serviceName : nameList){
		    	System.out.println(serviceName);
		    	
		    	//获取服务配置信息
		    	Collection<ServiceInstance<ThriftServicePayload>> serviceInstanceList = clientBootstrap.getServiceRegistry().queryForInstances(serviceName);
		    	if(serviceInstanceList != null && serviceInstanceList.size() > 0){
		    		for(ServiceInstance<ThriftServicePayload> serviceInstance : serviceInstanceList){
		    			String key = serviceInstance.getName() + ":" + serviceInstance.getId();
		    			System.out.println(key);
		    			
		    			if(!beanMap.containsKey(key)){
		    				//服务信息
		    				ServiceBean bean = new ServiceBean();
		    				bean.setName(serviceInstance.getName());
		    				bean.setId(serviceInstance.getId());
		    				bean.setAddress(serviceInstance.getAddress());
		    				bean.setPort(serviceInstance.getPort());
		    				bean.setInterfaceName(serviceInstance.getPayload().getInterfaceName());
		    				bean.setVersion(serviceInstance.getPayload().getVersion());
		    				
		    				ServiceType type = serviceInstance.getServiceType();
		    				if(type == ServiceType.DYNAMIC || type == ServiceType.DYNAMIC_SEQUENTIAL){
		    					bean.setType("动态");
		    				}else{
		    					bean.setType("静态");
		    				}

		    				String time = DatetimeUtil.formatDate(serviceInstance.getRegistrationTimeUTC(), DatetimeUtil.DEFAULT_PATTERN_DT);
		    				bean.setTime(time);
		    				
		    				beanMap.put(key, bean);
		    			}
		    		}
		    	}
		    }
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return beanMap;
	}
	
	public <T> T getServiceClient(Class<T> serviceClientClass){
		return clientBootstrap.getServiceClient(serviceClientClass);
	}
	
}
