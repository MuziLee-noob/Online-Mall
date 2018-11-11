app.controller('brandController', function($scope, $http, brandService) {
	// 查询品牌列表
	// $scope.findAll = function() {
	// brandService.findAll().success(function(data){
	// $scope.list = data;
	// });
	// }

	// 分页控件配置 currentPage:当前页 totalItems:总记录数 itemPerPage:每页记录数
	// perPageOptions:分页选项 onChange:当页码变更后，自动触发的方法
	$scope.paginationConf = {
		currentPage : 1,
		totalItems : 10,
		itemsPerPage : 10,
		perPageOptions : [ 10, 20, 30, 40, 50 ],
		onChange : function() {
			$scope.reloadList();// 重新加载
		}
	};

	// 刷新列表
	$scope.reloadList = function() {
		$scope.search($scope.paginationConf.currentPage,
				$scope.paginationConf.itemsPerPage);
	}

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
	$scope.selectIds = [];// 用户勾选的id集合

	$scope.updateSelection = function($event, id) {
		if ($event.target.checked) {
			$scope.selectIds.push(id);// push向集合添加元素
		} else {
			var index = $scope.selectIds.indexOf(id);// 查找值的位置
			$scope.selectIds.splice(index, 1);
		}
	}

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
