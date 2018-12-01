 //控制层 
app.controller('userController' ,function($scope,$controller   ,userService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
	//注册
	$scope.reg = function() {
		if ($scope.password != $scope.entity.password) {
			alert("密码输入不一致，请重新输入");
			$scope.password = "";
			$scope.entity.password = "";
			return;
		}
		userService.add($scope.entity, $scope.smscode).success(
			function(response) {
				alert(response.message);
			}
		)
	}
	
	$scope.createCode = function() {
		if ($scope.entity.phone == null || $scope.entity.phone == "") {
			alert("请填写验证码");
		}
		userService.createCode($scope.entity.phone).success(function(response) {
			alert(response.message);
		})
	}
});	
