package com.seasy.microservice.server.serviceimpl;

import org.apache.thrift.TException;

import com.seasy.microservice.api.Hello;
import com.seasy.microservice.api.Message;
import com.seasy.microservice.api.Response;
import com.seasy.microservice.core.common.ServiceAnnotation;

@ServiceAnnotation(serviceClass=Hello.class, version="1.0.0")
public class HelloServiceImpl implements Hello.Iface{
	@Override
	public String helloString(String param) throws TException {
		return param;
	}

	@Override
	public int helloInt(int param) throws TException {
		return param;
	}

	@Override
	public boolean helloBoolean(boolean param) throws TException {
		return param;
	}

	@Override
	public void helloVoid() throws TException {
		System.out.println("hello void");
	}
	
	@Override
	public Response sendMessage(Message message) throws TException {
		System.out.println(message.getType() + ", " + new String(message.getData()));
		
		Response response = new Response(0, "success");
		return response;
	}
	
}
