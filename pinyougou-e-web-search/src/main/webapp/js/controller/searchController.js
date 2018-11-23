app.controller("searchController", function($scope, searchService) {
	
	//定义搜索对象的结构
	$scope.searchMap={'keywords':'', 'category':'', 'brand':'', 'spec':{}};

	$scope.search = function() {
		searchService.search($scope.searchMap).success(
			function(response) {
			$scope.resultMap = response;
		});
	}
	// 添加搜索项
	$scope.addSearchItem = function(key, value) {
		
		if(key == 'category' || key == 'brand') { //如果用户点击的是分类或者品牌
			$scope.searchMap[key] = value;
		} else { //用户点击的是规格
			$scope.searchMap.spec[key] = value;
		}
		$scope.search();
	}

	$scope.removeSearchItem = function(key) {
		if(key == 'category' || key == 'brand') { //如果用户点击的是分类或者品牌
			$scope.searchMap[key] = "";
		} else { //用户点击的是规格
			delete $scope.searchMap.spec[key];
		}
		$scope.search();
	}
});
