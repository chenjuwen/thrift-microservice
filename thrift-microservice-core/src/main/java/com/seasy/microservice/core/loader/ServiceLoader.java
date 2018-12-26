package com.seasy.microservice.core.loader;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.reflections.Reflections;
import org.slf4j.Logger;

import com.seasy.microservice.core.LoggerFactory;
import com.seasy.microservice.core.common.ServiceAnnotation;
import com.seasy.microservice.core.common.ServiceInformation;
import com.seasy.microservice.core.utils.StringUtil;

/**
 * 加载thrift服务类
 */
public class ServiceLoader extends AbstractLoader<ConcurrentHashMap<String, ServiceInformation>> {
    private static final Logger logger = LoggerFactory.getLogger(ServiceLoader.class);

    @Override
    public ConcurrentHashMap<String, ServiceInformation> load(){
        ConcurrentHashMap<String, ServiceInformation> serviceInformationMap = new ConcurrentHashMap<String, ServiceInformation>();

        if(StringUtil.isNotEmpty(getBasePackages())){
            try {
                String[] packagesArray = getBasePackages().split(";");
                for(String packagePath : packagesArray){
                	serviceInformationMap.putAll(loadService(packagePath));
                }
            }catch (Exception ex){
                serviceInformationMap.clear();
                logger.error("load service error", ex);
            }
        }

        return serviceInformationMap;
    }
    
    /**
     * 根据包路径加载包含指定注解类的thrift服务类
     * @param packagePath 服务类所在的包路径
     */
    private ConcurrentHashMap<String, ServiceInformation> loadService(String packagePath) throws Exception {
    	ConcurrentHashMap<String, ServiceInformation> serviceInformationMap = new ConcurrentHashMap<String, ServiceInformation>();
    	
    	Reflections reflections = new Reflections(packagePath);
    	
    	//查找有指定注解类的服务类
		Set<Class<?>> serviceImplementClassSet = reflections.getTypesAnnotatedWith(ServiceAnnotation.class);
		for(Class<?> serviceImplementClass : serviceImplementClassSet){
			ServiceAnnotation serviceAnnotation = serviceImplementClass.getAnnotation(ServiceAnnotation.class);
			
			//服务相关信息封装在ServiceInformation类中
			ServiceInformation serviceInformation = new ServiceInformation();
			serviceInformation.setId(serviceInformation.getId());
			serviceInformation.setServiceClass(serviceAnnotation.serviceClass());
			serviceInformation.setVersion(serviceAnnotation.version());
			serviceInformation.setTimeout(serviceAnnotation.timeout());
			serviceInformation.setServiceImplementClassInstance(serviceImplementClass.newInstance());
			
			String key = serviceAnnotation.serviceClass().getName();
			serviceInformationMap.put(key, serviceInformation);
			logger.debug("Class [" + key + "] loaded!");
		}
		
		return serviceInformationMap;
    }

}
