package com.seasy.microservice.admin.controller;

import java.nio.ByteBuffer;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.seasy.microservice.admin.bean.ServiceBean;
import com.seasy.microservice.admin.service.ServiceDiscoveryService;
import com.seasy.microservice.api.CardReaderDevice;
import com.seasy.microservice.api.FingerScannerDevice;
import com.seasy.microservice.api.Hello;
import com.seasy.microservice.api.Message;
import com.seasy.microservice.api.Response;
import com.seasy.microservice.core.utils.JsonUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping("/service")
public class ServiceController {
	@Autowired
	private ServiceDiscoveryService serviceDiscoveryService;
	
	/**
	 * 获取服务信息列表
	 */
	@RequestMapping("/list")
    public String list(ModelMap map) {
		Map<String, ServiceBean> beanMap = serviceDiscoveryService.getServiceList();
		if(beanMap != null && !beanMap.isEmpty()){
			String jsonData = JSONArray.fromObject(beanMap.values()).toString();
			map.put("dataList", jsonData);
		}else{
			map.put("dataList", "[]");
		}
		
        return "service-list";
    }
	
	/**
	 * 测试服务的调用
	 */
	@RequestMapping(value="/test", method=RequestMethod.POST)
	@ResponseBody
    public String test(@RequestBody String data, ModelMap map) {
		JSONObject object = JsonUtil.string2object(data);
		String serviceName = JsonUtil.getString(object, "serviceName");
		
		try{	
			if(Hello.class.getSimpleName().equalsIgnoreCase(serviceName)){
				Hello.Client client = serviceDiscoveryService.getServiceClient(Hello.Client.class);
				System.out.println(client.helloString("Hello.Client"));
				
			}else if(CardReaderDevice.class.getSimpleName().equalsIgnoreCase(serviceName)){
				CardReaderDevice.Client client = serviceDiscoveryService.getServiceClient(CardReaderDevice.Client.class);

		        ByteBuffer byteBuffer = ByteBuffer.wrap("hello world".getBytes("UTF-8"));
		        Response response = client.sendMessage(new Message(1, byteBuffer));
		        System.out.println(response.getCode() + ", " + response.getMessage());
		        
			}else if(FingerScannerDevice.class.getSimpleName().equalsIgnoreCase(serviceName)){
				FingerScannerDevice.Client client = serviceDiscoveryService.getServiceClient(FingerScannerDevice.Client.class);
				System.out.println(client.helloString("FingerScannerDevice.Client"));
			}
			
			return "success";
		}catch(Exception ex){
			ex.printStackTrace();
		}
        return "error";
    }
	
}
