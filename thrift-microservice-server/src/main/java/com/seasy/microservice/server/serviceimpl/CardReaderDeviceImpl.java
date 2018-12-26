package com.seasy.microservice.server.serviceimpl;

import org.apache.thrift.TException;

import com.seasy.microservice.api.CardReaderDevice;
import com.seasy.microservice.api.Message;
import com.seasy.microservice.api.Response;
import com.seasy.microservice.core.common.ServiceAnnotation;

@ServiceAnnotation(serviceClass=CardReaderDevice.class, version="1.0.0")
public class CardReaderDeviceImpl implements CardReaderDevice.Iface{
	@Override
	public void init() throws TException {
		System.out.println("init...");
	}

	@Override
	public void unInit() throws TException {
		System.out.println("unInit...");
	}

	@Override
	public Response sendMessage(Message message) throws TException {
		System.out.println(message.getType() + ", " + new String(message.getData()));
		
		Response response = new Response(0, "succsss");
		return response;
	}
	
}
