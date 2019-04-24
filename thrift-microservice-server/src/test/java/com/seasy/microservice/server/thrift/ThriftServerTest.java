package com.seasy.microservice.server.thrift;

import java.lang.reflect.Constructor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

import com.seasy.microservice.api.CardReaderDevice;
import com.seasy.microservice.api.Hello;
import com.seasy.microservice.server.serviceimpl.CardReaderDeviceImpl;
import com.seasy.microservice.server.serviceimpl.HelloServiceImpl;

/**
 * 服务器端类
 */
public class ThriftServerTest {
	private static TServer server = null;
	
	/**
	 * 阻塞式多线程服务模型
	 */
	private static void start1()throws Exception{
		//阻塞式IO 
        TServerSocket serverTransport = new TServerSocket(Configuration.SERVER_PORT, Configuration.TIMEOUT);
        
        //处理器类
        TProcessor processor = new Hello.Processor<Hello.Iface>(new HelloServiceImpl());
        
        //线程池服务模型
        TThreadPoolServer.Args tArgs = new TThreadPoolServer.Args(serverTransport);
        tArgs.requestTimeoutUnit(TimeUnit.MILLISECONDS);
        tArgs.requestTimeout(5000);
        tArgs.minWorkerThreads(100);
        tArgs.maxWorkerThreads(1000);
        tArgs.protocolFactory(new TBinaryProtocol.Factory());
        tArgs.executorService(Executors.newFixedThreadPool(100));
        tArgs.processor(processor);
        
        server = new TThreadPoolServer(tArgs);
	}
	
	/**
	 * 非阻塞式多线程服务模型
	 */
	private static void start2()throws Exception{
		//非堵塞式传输通道
		TNonblockingServerTransport serverTransport = 
				new TNonblockingServerSocket(Configuration.SERVER_PORT, Configuration.TIMEOUT);
		
		//处理器
		TProcessor processor = new Hello.Processor<Hello.Iface>(new HelloServiceImpl());
		
		TNonblockingServer.Args args = new TNonblockingServer.Args(serverTransport);
		args.processor(processor);
		//使用非阻塞式IO，服务端和客户端需要指定TFramedTransport数据传输方式
		args.transportFactory(new TFramedTransport.Factory());
		args.protocolFactory(new TCompactProtocol.Factory()); //压缩二进制协议

		server = new TNonblockingServer(args);
	}
	
	private static void start4()throws Exception{
		TSSLTransportParameters params = new TSSLTransportParameters();
		params.setKeyStore("cert/server.jks", "dddddd", "SunX509", "JKS");
		
		TServerTransport serverTransport = 
				TSSLTransportFactory.getServerSocket(Configuration.SERVER_PORT, Configuration.TIMEOUT, null, params);
		
		//处理器
		TProcessor processor = new Hello.Processor<Hello.Iface>(new HelloServiceImpl());
		
		TThreadPoolServer.Args tArgs = new TThreadPoolServer.Args(serverTransport);
        tArgs.processor(processor);
        tArgs.protocolFactory(new TBinaryProtocol.Factory());
        
        server = new TThreadPoolServer(tArgs);
	}

	/**
	 * 通过反射构造TProcessor实例
	 * @param serviceImplements 服务接口类
	 * @param serviceImpl 服务实现类
	 */
    private static TProcessor getTProcessor(Class<?> serviceImplements, Object serviceImpl) throws Exception {
        String processorClazz = serviceImplements.getName() + "$Processor";
        String ifaceClazz = serviceImplements.getName() + "$Iface";
        
        Class<?> processorClass = Class.forName(processorClazz);
        Class<?> ifaceClass = Class.forName(ifaceClazz);
        
        Constructor<?> constructor = processorClass.getDeclaredConstructor(new Class[]{ifaceClass});
        TProcessor processor = (TProcessor) constructor.newInstance(new Object[]{serviceImpl});
        return processor;
    }
	
	private static void start5()throws Exception{
		TMultiplexedProcessor multiplexedProcessor = new TMultiplexedProcessor();
		multiplexedProcessor.registerProcessor("Hello", new Hello.Processor<Hello.Iface>(new HelloServiceImpl()));
		multiplexedProcessor.registerProcessor("CardReaderDevice", new CardReaderDevice.Processor<CardReaderDevice.Iface>(new CardReaderDeviceImpl()));
		//processor.registerProcessor("Hello", getTProcessor(Hello.class, new HelloServiceImpl()));
		
		//阻塞式IO 
        TServerSocket transport = new TServerSocket(Configuration.SERVER_PORT, Configuration.TIMEOUT);
        
        //多线程服务模型
        TThreadPoolServer.Args tArgs = new TThreadPoolServer.Args(transport);
        tArgs.requestTimeoutUnit(TimeUnit.MILLISECONDS);
        tArgs.requestTimeout(5000);
        tArgs.minWorkerThreads(100);
        tArgs.maxWorkerThreads(500);
        tArgs.protocolFactory(new TBinaryProtocol.Factory());
        tArgs.processor(multiplexedProcessor);
        
        server = new TThreadPoolServer(tArgs);
	}
	
	private static void start6()throws Exception{
		TMultiplexedProcessor multiplexedProcessor = new TMultiplexedProcessor();
		multiplexedProcessor.registerProcessor("Hello", new Hello.Processor<Hello.Iface>(new HelloServiceImpl()));
		
		TNonblockingServerTransport transport = 
				new TNonblockingServerSocket(Configuration.SERVER_PORT, Configuration.TIMEOUT);
		
		TThreadedSelectorServer.Args tArgs = new TThreadedSelectorServer.Args(transport);
		tArgs.processor(multiplexedProcessor);
		tArgs.transportFactory(new TFramedTransport.Factory());
		tArgs.protocolFactory(new TCompactProtocol.Factory());
		tArgs.selectorThreads(5);
		tArgs.workerThreads(50);
        
		TServer server = new TThreadedSelectorServer(tArgs);
        server.serve();
	}
	
	public static void main(String[] args) {
		try{
//			start4();
			start6();
			
			if(server != null){
				System.out.println("Start server on port " + Configuration.SERVER_PORT + " ...");
				server.serve();
			}
	        
		}catch(Exception ex){
			ex.printStackTrace();
			
			if(server != null){
				server.stop();
			}
		}
	}
	
}
