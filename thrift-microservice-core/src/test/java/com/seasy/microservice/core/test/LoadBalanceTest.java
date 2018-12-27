package com.seasy.microservice.core.test;

import java.util.Collection;

import org.apache.curator.x.discovery.ServiceInstance;

import com.seasy.microservice.core.ClientBootstrap;
import com.seasy.microservice.core.ThriftClientBootstrap;
import com.seasy.microservice.core.common.ThriftServicePayload;
import com.seasy.microservice.core.loadbalance.LoadBalance;
import com.seasy.microservice.core.loadbalance.RoundRobinLoadBalance;

public class LoadBalanceTest {
	public static void main(String[] args) {
		ClientBootstrap clientBootstrap = null;
		try{
			String registryAddress = "192.168.134.134:2181";
			String serviceName = "Hello";
			
			clientBootstrap = new ThriftClientBootstrap(registryAddress);
			clientBootstrap.start();
			
			LoadBalance lb = new RoundRobinLoadBalance();
			Collection<ServiceInstance<ThriftServicePayload>> list = clientBootstrap.getServiceRegistry().queryForInstances(serviceName);
			ServiceInstance<ThriftServicePayload> s1 = lb.select(list, serviceName);
			System.out.println(s1.getAddress() + ", " + s1.getPort());
			
			s1 = lb.select(list, serviceName);
			System.out.println(s1.getAddress() + ", " + s1.getPort());
			
			s1 = lb.select(list, serviceName);
			System.out.println(s1.getAddress() + ", " + s1.getPort());
			
			s1 = lb.select(list, serviceName);
			System.out.println(s1.getAddress() + ", " + s1.getPort());
			
			s1 = lb.select(list, serviceName);
			System.out.println(s1.getAddress() + ", " + s1.getPort());
			
			s1 = lb.select(list, serviceName);
			System.out.println(s1.getAddress() + ", " + s1.getPort());
			
			s1 = lb.select(list, serviceName);
			System.out.println(s1.getAddress() + ", " + s1.getPort());
			
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(clientBootstrap != null){
		        clientBootstrap.stop();
			}
		}
	}
}
