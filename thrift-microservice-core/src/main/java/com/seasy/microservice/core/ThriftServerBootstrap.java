package com.seasy.microservice.core;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceType;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.ServerContext;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServerEventHandler;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;

import com.seasy.microservice.core.common.LoggerFactory;
import com.seasy.microservice.core.common.ServiceInformation;
import com.seasy.microservice.core.common.ThriftServicePayload;
import com.seasy.microservice.core.loader.ServiceLoader;
import com.seasy.microservice.core.utils.EnvUtil;
import com.seasy.microservice.core.utils.StringUtil;

public class ThriftServerBootstrap extends AbstractBootstrap implements ServerBotstrap {
	private static final Logger logger = LoggerFactory.getLogger(ThriftServerBootstrap.class);
	
	private ProviderHelper providerHelper;
	private String ip; //服务器ip
	private int port; //服务监听端口
	
	private String serviceBasePackage; //服务实现类所在的根包路径
	private ConcurrentHashMap<String, ServiceInformation> serviceInformationMap;
	
	private TMultiplexedProcessor multiplexedProcessor;
	private TNonblockingServerSocket serverSocket = null;
	private TServer tserver = null;
	
	private AtomicInteger connectCount = new AtomicInteger(); //客户端连接数
	
	public ThriftServerBootstrap(int port, String serviceBasePackage, String registryAddress){
		super(registryAddress);
		this.ip = EnvUtil.getLocalIp();
		this.port = port;
		this.serviceBasePackage = serviceBasePackage;
	}

	/**
	 * 非阻塞式的多线程服务模型
	 */
	@Override
	public void start() throws Exception {
		logger.debug("start ThriftServerBootstrap...");
		super.start();
		
		initProviderHelper();
		
		loadBusinessService();
		
		buildMultiplexedProcessor();
		
		registerBusinessService();
		
		startServer();
	}
	
	/**
	 * 初始化根znode
	 */
	private void initProviderHelper()throws Exception{
		providerHelper = new ProviderHelper(serviceRegistry.getCuratorHelper());
		providerHelper.initRootZnode();
	}
	
	/**
	 * 加载业务服务实现类
	 */
	private void loadBusinessService(){
		ServiceLoader loader = new ServiceLoader();
		loader.setBasePackages(this.serviceBasePackage);
		serviceInformationMap = loader.load();
	}
	
	/**
	 * 构造MultiplexedProcessor对象
	 */
	private void buildMultiplexedProcessor(){
		multiplexedProcessor = new TMultiplexedProcessor();
		
    	for(Iterator<String> it=serviceInformationMap.keySet().iterator(); it.hasNext(); ){
    		String serviceClassFullname = it.next();
    		logger.debug("serviceClassFullname=" + serviceClassFullname);
    		
    		//服务名
    		String serviceName = serviceClassFullname.substring(serviceClassFullname.lastIndexOf(".")+1);
    		logger.debug("serviceName=" + serviceName);
    		
    		Object serviceImplementClassInstance = serviceInformationMap.get(serviceClassFullname).getServiceImplementClassInstance();
    		
    		TProcessor serviceProcessor = createServiceProcessor(serviceClassFullname, serviceImplementClassInstance);
    		if(serviceProcessor != null){
    			//以接口主类的SimpleName作为服务名
    			multiplexedProcessor.registerProcessor(serviceName, serviceProcessor);
    			serviceInformationMap.get(serviceClassFullname).setProcessorRegistered(true);
    			logger.info("Processor [" + serviceName + "] published!");
    		}
    	}
	}
	
	/**
	 * 创建服务的TProcessor实例对象
	 * 
	 * @param serviceClassFullname 服务接口全限定类名
	 * @param serviceImplementClassInstance 服务实现类
	 */
    private TProcessor createServiceProcessor(String serviceClassFullname, Object serviceImplementClassInstance){
    	try{
	        String processorClassName = serviceClassFullname + "$Processor";
	        String ifaceClassName = serviceClassFullname + "$Iface";
	        
	        Class<?> processorClass = Class.forName(processorClassName);
	        Class<?> ifaceClass = Class.forName(ifaceClassName);
	        
	        Constructor<?> constructor = processorClass.getDeclaredConstructor(new Class[]{ifaceClass});
	        TProcessor processor = (TProcessor) constructor.newInstance(new Object[]{serviceImplementClassInstance});
	        
	        return processor;
	        
    	}catch(Exception ex){
    		logger.error("create service[" + serviceClassFullname + "] processor error", ex);
    	}
    	return null;
    }
    
    /**
     * 注册业务服务到注册中心
     */
    private void registerBusinessService(){
    	for(Iterator<String> it=serviceInformationMap.keySet().iterator(); it.hasNext(); ){
    		String serviceClassFullname = it.next();
    		String serviceName = serviceClassFullname.substring(serviceClassFullname.lastIndexOf(".")+1);
    		ServiceInformation serviceInformation = serviceInformationMap.get(serviceClassFullname);
    		
    		if(serviceInformation.isProcessorRegistered()){
    			try{
    				//构造ServiceInstance对象，该对象表示一个业务服务，用于存储业务服务相关的参数数据
	    			ServiceInstance<ThriftServicePayload> serviceInstance = ServiceInstance.<ThriftServicePayload>builder()
	    				.name(serviceName)
	    				.id(StringUtil.isEmpty(serviceInformation.getId()) ? serviceName : serviceInformation.getId())
	    				.address(ip)
	    				.port(getPort())
	    				.payload(new ThriftServicePayload(serviceInformation.getVersion(), serviceInformation.getServiceClass().getName()))
	    				.registrationTimeUTC(System.currentTimeMillis())
	    				.serviceType(ServiceType.DYNAMIC)
	    				.build();
	    	    	
	    	    	serviceRegistry.registerService(serviceInstance);
	    	    	serviceInformation.setServiceRegistered(true);
	    	    	logger.info("Service [" + serviceName + "] registered!");
	    	    	
    			}catch(Exception ex){
    				logger.error("register service[" + serviceName + "] error", ex);
    			}
    		}
    	}
    }
    
    /**
     * 启动Thrift Server
     */
    private void startServer()throws Exception{
		serverSocket = new TNonblockingServerSocket(getPort());
		
		TNonblockingServer.Args tArgs = new TNonblockingServer.Args(serverSocket);
        tArgs.processor(multiplexedProcessor);
        tArgs.transportFactory(new TFramedTransport.Factory());
        tArgs.protocolFactory(new TCompactProtocol.Factory());

        tserver = new TNonblockingServer(tArgs);
        tserver.setServerEventHandler(new DefaultServerEventHandler());
        tserver.serve();
    }

	/**
	 * 停止thrift服务端
	 */
	@Override
	public void stop(){
		super.stop();
		
		if(serverSocket != null){
			serverSocket.close();
			serverSocket = null;
		}
		
		if(tserver != null){
			tserver.stop();
			tserver = null;
		}
		
		serviceInformationMap.clear();
	}
	
	class DefaultServerEventHandler implements TServerEventHandler{
		@Override
		public void preServe() {
			if(isRegister()){
				providerHelper.register(ip, String.valueOf(port));
			}
			
	        logger.info("start server at port " + getPort());
		}
		
		@Override
		public ServerContext createContext(TProtocol input, TProtocol output) {
			System.out.println("创建客户端连接");
			connectCount.incrementAndGet();
			return null;
		}
		
		@Override
		public void processContext(ServerContext serverContext, TTransport inputTransport, TTransport outputTransport) {
			//System.out.println("processContext");
		}
		
		@Override
		public void deleteContext(ServerContext serverContext, TProtocol input, TProtocol output) {
			System.out.println("删除客户端连接");
			connectCount.decrementAndGet();
		}
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public AtomicInteger getConnectCount() {
		return connectCount;
	}
	
}
