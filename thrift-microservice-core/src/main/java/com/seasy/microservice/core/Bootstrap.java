package com.seasy.microservice.core;

public interface Bootstrap {
	void start() throws Exception;
	void stop();
	boolean isRegister();
	void setRegister(boolean register);
}