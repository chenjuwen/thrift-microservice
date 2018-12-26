package com.seasy.microservice.core.common;

import java.io.Serializable;

/**
 * 用户自定义属性类
 */
public class ThriftServicePayload implements Serializable{
	private static final long serialVersionUID = -8963742559418457439L;
	private String version; //服务版本号
	private String interfaceName; //服务接口名
	
	public ThriftServicePayload(){
		
	}
	
	public ThriftServicePayload(String version, String interfaceName){
		this.version = version;
		this.interfaceName = interfaceName;
	}
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	@Override
	public String toString() {
		return "ServicePayload [version=" + this.version + ", interfaceName=" + this.interfaceName + "]";
	}
	
}
