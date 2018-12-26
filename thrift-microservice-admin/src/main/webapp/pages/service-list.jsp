<%@page language="java" pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<!DOCTYPE html>
<html>
<head>
  <title>service list</title>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <link rel="stylesheet" href="element-ui/theme-chalk/index.css">
  <script type="text/javascript" src="element-ui/vue.min.js"></script>
  <script type="text/javascript" src="element-ui/index.js"></script>
  <script type="text/javascript" src="js/axios.min.js"></script>
  <script type="text/javascript" src="js/jquery.min-v1.11.3.js"></script>

  <style>
  </style>
</head>
<body>
	<div id="subApp">
		<template>
		  <el-table :data="dataList" border stripe style="width: 100%" :default-sort = "{prop: 'address', order: 'ascending'}">
		    <el-table-column sortable prop="address" label="服务器地址" width="130"></el-table-column>
		    <el-table-column prop="port" label="监听端口" width="80"></el-table-column>
		    <el-table-column sortable prop="name" label="服务名" width="160"></el-table-column>
		    <!-- <el-table-column prop="id" label="服务ID" width="150"></el-table-column> -->
		    <el-table-column prop="interfaceName" label="服务接口名" width="350"></el-table-column>
		    <el-table-column prop="version" label="版本号" width="70"></el-table-column>
		    <el-table-column prop="type" label="服务类型" width="80"></el-table-column>
		    <el-table-column sortable prop="time" label="时间戳"></el-table-column>
		    
		    <el-table-column label="操作" width="80">
		      <template slot-scope="scope">
		        <el-button
		          size="mini"
		          @click="handleService(scope.$index, scope.row)">测试</el-button>
		      </template>
		    </el-table-column>
    
		  </el-table>
		</template>
	</div>

	<script type="text/javascript">
		var subVM = new Vue({
			el: '#subApp',
			data: function() {
				return {
					dataList:[
						{name:"Hello", id:"", address:"127.0.0.1", port:20001, interfaceName:"com.seasy.microservice.api.Hello", version:"1.0.0", type:"动态", time:"2018-12-23 21:12:10"}
					]
				}
			},
			methods: {
				handleService:function(index, rowData){
					//alert(index + ", " + JSON.stringify(rowData));
					axios.post("service/test",{
						serviceName: rowData.name
					})
					.then(function(response){
						if(response.status == 200){
							subVM.$message({
								message: response.data,
								type: 'success',
								duration: 2000
							});
						}else{
							alert(JSON.stringify(response));
						}
					})
					.catch(function(error){
						console.log(JSON.stringify(error));
					});
				}
			}
		});

		subVM.dataList = ${dataList};
	</script>
	
</body>
</html>