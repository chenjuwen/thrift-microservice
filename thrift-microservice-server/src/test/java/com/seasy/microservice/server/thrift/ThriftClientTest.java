package com.seasy.microservice.server.thrift;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;

import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TNonblockingTransport;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import com.seasy.microservice.api.CardReaderDevice;
import com.seasy.microservice.api.Hello;
import com.seasy.microservice.api.Message;

public class ThriftClientTest {
	private static TTransport transport = null;
	private static Hello.Client client = null;
	private static CountDownLatch latch = new CountDownLatch(4);
	
	/**
	 * 堵塞式
	 */
	private static void client1()throws Exception{
		transport = new TSocket(Configuration.HOST, Configuration.SERVER_PORT, Configuration.TIMEOUT);
        transport.open();
        
        TProtocol protocol = new TBinaryProtocol(transport);
        client = new Hello.Client(protocol);
        
        System.out.println(client.helloBoolean(true));
        System.out.println(client.helloInt(99));
        System.out.println(client.helloString("hello string"));
        client.helloVoid();
        
        ByteBuffer data = ByteBuffer.wrap("hello world".getBytes("UTF-8"));
        client.sendMessage(new Message(1, data));
		
        System.out.println("end");
	}

	/**
	 * 非堵塞式
	 */
	private static void client2()throws Exception{
		transport = new TFramedTransport(new TSocket(Configuration.HOST, Configuration.SERVER_PORT, Configuration.TIMEOUT));
        transport.open();
        
        TProtocol protocol = new TCompactProtocol(transport); //压缩二进制协议
        client = new Hello.Client(protocol);
        
        System.out.println(client.helloBoolean(true));
        System.out.println(client.helloInt(99));
        System.out.println(client.helloString("hello string"));
        client.helloVoid();
		
        System.out.println("end");
	}
	
	/**
	 * 非堵塞式、异步调用
	 */
	private static void client3()throws Exception{        
		//异步调用管理器  
        TAsyncClientManager asyncClientManager = new TAsyncClientManager();
        
        final TNonblockingTransport nonblockingTransport = new TNonblockingSocket(Configuration.HOST, Configuration.SERVER_PORT, Configuration.TIMEOUT);
        
        TProtocolFactory protocolFactory = new TCompactProtocol.Factory();
        
        final Hello.AsyncClient client = new Hello.AsyncClient(protocolFactory, asyncClientManager, nonblockingTransport);
        
        client.helloBoolean(true, new AsyncMethodCallback<Boolean>() {
        	@Override
        	public void onComplete(Boolean response) {
        		try {
        			//boolean
					System.out.println(response);
					latch.countDown();
					
					client.helloInt(99, new AsyncMethodCallback<Integer>() {
			        	@Override
			        	public void onComplete(Integer response) {
			        		try {
			        			//int
								System.out.println(response);
								latch.countDown();
								
								client.helloString("hello string", new AsyncMethodCallback<String>() {
						        	@Override
						        	public void onComplete(String response) {
						        		try {
						        			//string
											System.out.println(response);
											latch.countDown();
											
											client.helloVoid(new AsyncMethodCallback<Void>() {
									        	@Override
									        	public void onComplete(Void response) {
													latch.countDown();
									        	}
									        	@Override
									        	public void onError(Exception ex) {
									        		ex.printStackTrace();
													latch.countDown();
									        	}
											});
										} catch (TException e) {
											e.printStackTrace();
											latch.countDown();
										}
						        	}
						        	@Override
						        	public void onError(Exception ex) {
						        		ex.printStackTrace();
						        	}
								});
							} catch (TException e) {
								e.printStackTrace();
								latch.countDown();
							}
			        	}
			        	@Override
			        	public void onError(Exception ex) {
			        		ex.printStackTrace();
			        	}
					});
					
				} catch (TException e) {
					e.printStackTrace();
					latch.countDown();
				}
        	}
        	@Override
        	public void onError(Exception ex) {
        		ex.printStackTrace();
        	}
		});
        
        latch.await();
        
        System.out.println("end");
	}
	
	private static void client4()throws Exception{
		TSSLTransportParameters params = new TSSLTransportParameters();
		params.setTrustStore("cert/DBSBank-SG-Sub-CA.jks", "x01svtmaapp1a.uat.dbs.com", "SunX509", "JKS");
		
		transport = TSSLTransportFactory
				.getClientSocket(Configuration.HOST, Configuration.SERVER_PORT, Configuration.TIMEOUT, params);
		
		TProtocol protocol = new TBinaryProtocol(transport);
        client = new Hello.Client(protocol);
        
        System.out.println(client.helloBoolean(true));
        System.out.println(client.helloInt(99));
        System.out.println(client.helloString("hello string"));
        client.helloVoid();
		
        System.out.println("end");
	}
	
	private static void client5()throws Exception{
		transport = new TSocket(Configuration.HOST, Configuration.SERVER_PORT, Configuration.TIMEOUT);
        TProtocol protocol = new TBinaryProtocol(transport);
        
        TMultiplexedProtocol helloProtocol = new TMultiplexedProtocol(protocol, "Hello");
        TMultiplexedProtocol deviceProtocol = new TMultiplexedProtocol(protocol, "CardReaderDevice");
        
        Hello.Client client = new Hello.Client(helloProtocol);
        CardReaderDevice.Client device = new CardReaderDevice.Client(deviceProtocol);
        
        transport.open();
        
        //Hello
        System.out.println(client.helloString("hello string"));
        ByteBuffer data = ByteBuffer.wrap("hello world".getBytes("UTF-8"));
        client.sendMessage(new Message(1, data));
        
        //CardReaderDevice
        device.sendMessage(new Message(2, data));
	}
	
	public static void main(String[] args) {
		try{
			client5();
	        System.out.println("end");
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(transport != null){
				transport.close();
			}
		}
	}
	
}
