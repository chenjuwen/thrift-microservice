package com.seasy.microservice.server.serviceimpl;

import org.apache.thrift.TException;

import com.seasy.microservice.api.FingerScannerDevice;
import com.seasy.microservice.core.common.ServiceAnnotation;


@ServiceAnnotation(serviceClass=FingerScannerDevice.class, version="1.0.0")
public class FingerScannerDeviceImpl implements FingerScannerDevice.Iface{
	@Override
	public void init() throws TException {
		System.out.println("init...");
	}

	@Override
	public void unInit() throws TException {
		System.out.println("unInit...");
	}

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
		System.out.println("helloVoid");
	}
	
}
