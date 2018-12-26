package com.seasy.microservice.admin.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.seasy.microservice.admin.bean.ConsumerBean;
import com.seasy.microservice.admin.service.ServiceDiscoveryService;

import net.sf.json.JSONArray;

@Controller
@RequestMapping("/consumer")
public class ConsumerController {
	@Autowired
	private ServiceDiscoveryService serviceDiscoveryService;
	
	@RequestMapping("/list")
    public String list(ModelMap map) {
		Map<String, ConsumerBean> beanMap = serviceDiscoveryService.getConsumerList();
		if(beanMap != null && !beanMap.isEmpty()){
			String jsonData = JSONArray.fromObject(beanMap.values()).toString();
			map.put("dataList", jsonData);
		}else{
			map.put("dataList", "[]");
		}
		
        return "consumer-list";
    }
	
}
