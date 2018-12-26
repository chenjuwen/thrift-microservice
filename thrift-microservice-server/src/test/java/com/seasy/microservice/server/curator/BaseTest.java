package com.seasy.microservice.server.curator;

import org.apache.curator.test.TestingServer;

public class BaseTest {
	private static TestingServer testingServer = null;
	private static boolean mock = false;
	public static String connectString = "192.168.134.134:2181";
	
	public static void startTestingServer()throws Exception{
		if(mock){
			testingServer = new TestingServer();
			testingServer.start();
			
			connectString = testingServer.getConnectString();
		}
	    System.out.println(connectString);
	}
	
	public static void stopTestingServer(){
		try{
			if(testingServer != null){
				testingServer.close();
				testingServer = null;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
}
