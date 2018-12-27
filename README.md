# thrift-microservice  
基于Thrift实现的服务自动注册与发现组件  

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
