package com.seasy.microservice.core.common;

import java.io.File;

import org.slf4j.Logger;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;

/**
 * 日志工厂类
 */
public class LoggerFactory {
	public static final String WINDOWS_LOG_CONFIG_FILE = "config/logback.xml";
	static{
		if(isWindowsOS()){
	        try {
	        	doConfigure(WINDOWS_LOG_CONFIG_FILE);
	        } catch (Exception ex) {
				ex.printStackTrace();
	        }
		}
	}
	
	public static void doConfigure(String logConfigFile) throws Exception {
		LoggerContext loggerContext = (LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(loggerContext);
        loggerContext.reset();
        
        configurator.doConfigure(new File(logConfigFile));
	}
	
	public static boolean isWindowsOS(){
		String osName = System.getProperty("os.name").toLowerCase();
		if(osName.indexOf("windows") >= 0){
			return true;
		}
		return false;
	}
	
	public static Logger getLogger(Class<?> clazz){
		return org.slf4j.LoggerFactory.getLogger(clazz);
	}
	
}
