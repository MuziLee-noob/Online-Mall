app.controller('brandController', function($scope, $controller, brandService) {
	
	$controller('baseController', {$scope:$scope});//让两个scope通用
	
	
	// 查询品牌列表
	// $scope.findAll = function() {
	// brandService.findAll().success(function(data){
	// $scope.list = data;
	// });
	// }

	

	// $scope.findPage = function(page, size) {
	// brandService.findPage(page, size)
	// .success(function(data) {
	// $scope.list = data.rows;//显示当前页数据
	// $scope.paginationConf.totalItems = data.total;//更新总记录数
	// })
	// };

	// 增加/修改功能
	$scope.save = function() {
		var object = null;// 方法名
		if ($scope.entity.id != null) {
			object = brandService.update($scope.entity);
		} else {
			object = brandService.add($scope.entity);
		}

		object.success(function(response) {
			if (response.success) {
				$scope.reloadList();
			} else {
				alert(response.message);
			}
		});
	}

	// 根据id查询
	$scope.findOne = function(id) {
		brandService.findOne(id).success(function(data) {
			$scope.entity = data;
		});
	}

	// 删除
	

	$scope.dele = function() {
		if ($scope.selectIds.length > 0) {
			if (confirm('确定要删除以下id吗\n' + $scope.selectIds)) {
				brandService.dele($scope.selectIds).success(function(response) {
					if (response.success) {
						$scope.reloadList();
					} else {
						alert(response.message);
					}
				});
			}
		}
	}

	$scope.searchEntity = {};

	// 搜索
	$scope.search = function(page, size) {
		brandService.search(page, size, $scope.searchEntity).success(
				function(data) {
					$scope.list = data.rows;// 显示当前页数据
					$scope.paginationConf.totalItems = data.total;// 更新总记录数
				})
	}

});
