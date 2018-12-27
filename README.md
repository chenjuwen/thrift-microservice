# thrift-microservice  
基于Thrift实现的服务自动注册与发现组件  

# 技术栈  
zookeeper-3.4.13  
thrift-0.11.0  
curator-2.13.0  

# 特性  
1、服务集群部署：同一个服务可以部署到多台服务器上，注册中心维护一个服务的多份payload信息  
1、客户端软负载均衡：暂支持随机和轮询两种方式  
3、服务提供者和服务消费者可选择是否要注册到注册中心  
4、业务服务实现类自动加载和注册  
5、一个端口监听多个业务服务  

# 运行步骤  
1、搭建并启动Zookeeper服务器  
2、运行thrift-microservice-server  
修改ServerMain类的注册中心地址，并运行该类，启动服务提供者程序  
3、运行thrift-microservice-client  
修改ClientMain类的注册中心地址，并运行该类，启动服务消费者程序，测试服务调用  
4、运行thrift-microservice-admin  
修改application.properties文件的注册中心地址，运行Main类，启动服务监控程序  
浏览器访问 http://localhost:7070/thrift-microservice-admin/index

# 管理中心截图  
<img src="https://github.com/chenjuwen/thrift-microservice/blob/master/doc/111.jpg" />
<img src="https://github.com/chenjuwen/thrift-microservice/blob/master/doc/222.jpg" />
<img src="https://github.com/chenjuwen/thrift-microservice/blob/master/doc/333.jpg" />
