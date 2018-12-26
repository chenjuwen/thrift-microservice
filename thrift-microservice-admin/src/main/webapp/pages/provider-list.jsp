<%@page language="java" pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<!DOCTYPE html>
<html>
<head>
  <title>provider list</title>
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
		  <el-table :data="dataList" border stripe style="width: 100%" :default-sort = "{prop: 'time', order: 'descending'}">
		    <el-table-column sortable prop="address" label="服务器地址" width="200"></el-table-column>
		    <el-table-column sortable prop="port" label="监听端口" width="150"></el-table-column>
		    <el-table-column sortable prop="time" label="启动时间"></el-table-column>
		  </el-table>
		</template>
	</div>

	<script type="text/javascript">
		var subVM = new Vue({
			el: '#subApp',
			data: function() {
				return {
					dataList:[
						{address:"127.0.0.1", port:"20001", time:"2018-12-23 21:12:10"},
						{address:"192.168.134.134", port:"20002", time:"2018-12-23 17:30:10"}
					]
				}
			},
			methods: {
				
			}
		});

		subVM.dataList = ${dataList};
	</script>
	
</body>
</html>