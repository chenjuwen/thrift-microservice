<%@page language="java" pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<!DOCTYPE html>
<html>
<head>
  <title>thrift-microservice-admin</title>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <link rel="stylesheet" href="element-ui/theme-chalk/index.css">
  <script type="text/javascript" src="element-ui/vue.min.js"></script>
  <script type="text/javascript" src="element-ui/index.js"></script>
  <script type="text/javascript" src="js/axios.min.js"></script>
  <script type="text/javascript" src="js/jquery.min-v1.11.3.js"></script>

  <style>
		body {
			margin: 0;
			padding: 0;
		}
		
		/* header */
		.el-header {
			padding: 0 10px;
		}
		
		.header {
			background-color: #4682B4;
			border-bottom: 1px solid #e6e6e6;
		}
		
		.logo-title {
			width: 220px;
			height: 100%;
			font-size: 28px;
			font-weight: 600;
			font-family: 黑体;
			float: left;
			color: #FFFFFF;
			padding-top: 15px;
		}
		
		.top-menu {
			width: 600px;
			float: left;
		}
		
		.el-menu {
			background-color: #4682B4;
			border-right: 0;
		}
		
		.el-menu--horizontal>.el-menu-item {
			font-weight: 600;
			font-size: 18px;
		}
		
		/* main container */
		.el-main {
			padding: 10px 10px 10px 10px;
		}
  </style>
</head>
<body>
	<div id="app">
		<el-container>
			<el-header height="61px" class="header">
				<div class="logo-title">ADMIN SYSTEM</div>
				
				<div class="top-menu">
					<el-menu :default-active="currentCourseIndex" 
						text-color="#FFFFFF"
						active-text-color="#EEEE00"
						background-color="#4682B4"
						mode="horizontal" 
						@select="topSelectHandler">
						<el-menu-item index="99">主页</el-menu-item>
						<el-menu-item :index="index" v-for="(item, index) in topmenus">
							{{ item.name }}
						</el-menu-item>
					</el-menu>
				</div>
			</el-header>

			<el-container>
				<el-main id="mainContainer">
					
				</el-main>
			</el-container>
		</el-container>
	</div>

	<script type="text/javascript">
		var vm = new Vue({
			el: '#app',
			data: function() {
				return {
					defaultIndex: "99",
					currentCourseIndex: this.defaultIndex,
					topmenus: [
						{"name":"服务提供者", "action":"provider/list"},
						{"name":"服务消费者", "action":"consumer/list"},
						{"name":"服务列表", "action":"service/list"}
					]
				}
			},
			methods: {
				topSelectHandler: function(index, indexPath){
					this.currentCourseIndex = index;

					if(this.currentCourseIndex != this.defaultIndex){
						var url = this.topmenus[index].action;
						
						axios.post(url, {})
						.then(function (response) {
							jQuery("#mainContainer").html(response.data);
						})
						.catch(function (error) {
							console.log(error);
						});
					}else{
						jQuery("#mainContainer").html("");
					}
				}
			}
		});
	</script>
	
</body>
</html>