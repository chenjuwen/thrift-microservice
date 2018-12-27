package com.seasy.microservice.client;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import com.seasy.microservice.api.CardReaderDevice;
import com.seasy.microservice.api.Hello;
import com.seasy.microservice.api.Message;
import com.seasy.microservice.api.Response;
import com.seasy.microservice.core.ClientBootstrap;
import com.seasy.microservice.core.ThriftClientBootstrap;

public class ClientMain {
	public static void main(String[] args) throws Exception {
		ClientBootstrap clientBootstrap = null;
		try{
			String registryAddress = "192.168.134.134:2181";
			
			clientBootstrap = new ThriftClientBootstrap(registryAddress);
			clientBootstrap.start();
			
			//Hello.Client
			Hello.Client client = clientBootstrap.getServiceClient(Hello.Client.class);
			
			if(client != null){
		        System.out.println(client.helloBoolean(true));
		        System.out.println(client.helloInt(99));
		        System.out.println(client.helloString("hello string"));
		        client.helloVoid();
		        
		        ByteBuffer data = ByteBuffer.wrap("hello world".getBytes("UTF-8"));
		        Response response = client.sendMessage(new Message(1, data));
		        System.out.println(response.getCode() + ", " + response.getMessage());
			}
			
	        //CardReaderDevice.Client
	        Object object = clientBootstrap.getServiceClient("CardReaderDevice");
	        if(object != null){
	        	CardReaderDevice.Client client2 = (CardReaderDevice.Client)object;
	        	
		        client2.init();
		        client2.unInit();
	
		        ByteBuffer data2 = ByteBuffer.wrap("CardReaderDevice sendMessage".getBytes("UTF-8"));
		        Response response = client2.sendMessage(new Message(2, data2));
		        System.out.println(response.getCode() + ", " + response.getMessage());
	        }
	        
	        TimeUnit.SECONDS.sleep(60);
	        System.out.println("OK");
	        
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(clientBootstrap != null){
		        clientBootstrap.stop();
			}
		}
	}
	
}
