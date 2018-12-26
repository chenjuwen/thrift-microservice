package com.seasy.microservice.core.utils;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import org.apache.commons.lang3.RandomStringUtils;


public class EnvUtil {
	/**
	 * 获取本机IP地址
	 */
	public static String getLocalIp(){
		String hostAddress = "";
		
		String osName = System.getProperty("os.name").toLowerCase();
		System.out.println(osName);
		
		if(osName.indexOf("windows") >= 0){
			try {     
		       if(StringUtil.isEmpty(hostAddress)){
		    	   hostAddress = InetAddress.getLocalHost().getHostAddress();
		       }
			} catch (Exception ex) {  
			   ex.printStackTrace();
			} 
			
		}else{
			try {
				for (Enumeration<NetworkInterface> enum1 = NetworkInterface.getNetworkInterfaces(); enum1.hasMoreElements();) {
			    	NetworkInterface networkInterface = enum1.nextElement();
//			    	System.out.println(networkInterface.getName() + ", " + networkInterface.getDisplayName()
//			    	+ ", " + networkInterface.isLoopback() + ", " + networkInterface.isPointToPoint()
//			    	 + ", " + networkInterface.isUp() + ", " + networkInterface.isVirtual());
			    	
			    	for (Enumeration<InetAddress> enum2 = networkInterface.getInetAddresses(); enum2.hasMoreElements();) {
			    		InetAddress inetAddress = enum2.nextElement();
						
			    		if(!"127.0.0.1".equalsIgnoreCase(inetAddress.getHostAddress())
			    				&& inetAddress.getHostAddress().split("\\.").length == 4){ //IPv4
							System.out.println("HostName: " + inetAddress.getHostName());
							System.out.println("HostAddress: " + inetAddress.getHostAddress());
							System.out.println("CanonicalHostName: " + inetAddress.getCanonicalHostName());
							
							System.out.println(inetAddress.isAnyLocalAddress() + ", " + inetAddress.isLoopbackAddress()
							 + ", " + inetAddress.isLinkLocalAddress() + ", " + inetAddress.isMulticastAddress()
							 + ", " + inetAddress.isSiteLocalAddress() + "\n");
							
			    			if (!inetAddress.isAnyLocalAddress() && !inetAddress.isLoopbackAddress()
			            		   && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
			    				hostAddress = inetAddress.getHostAddress();
			    				//可能会包含keepalived的虚拟IP
			    				//break;
			    			}
			    		}
			    	}
		           
			    	if(StringUtil.isNotEmpty(hostAddress)){
			    		break;
		           	}
				}
			} catch (Exception ex) {
			   ex.printStackTrace();
			}
		}
		
		System.out.println("local ip: " + hostAddress);
		return hostAddress;
	}
	
	/**
	 * 获取进程号
	 */
	public static String getProcessID(){
		String name = ManagementFactory.getRuntimeMXBean().getName();
		if(name != null && name.indexOf("@") > 0){
			name = name.substring(0, name.indexOf("@"));
		}else{
			name = String.valueOf(RandomStringUtils.randomNumeric(5));
		}
		return name;
	}
	
	public static void main(String[] args) {
		System.out.println(EnvUtil.getLocalIp());
	}
	
}
