app.controller("searchController", function($scope, searchService, $location) {
	
	//定义搜索对象的结构
	$scope.searchMap={'keywords':'', 'category':'', 'brand':'', 'spec':{}, 'price':'', 'pageNo': 1, 'pageSize': 20, 'sort':'', 'sortField':''};

	$scope.search = function() {
		$scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);//将字符串转换为数字
		searchService.search($scope.searchMap).success(
			function(response) {
			$scope.resultMap = response;
			
			buildPageLabel();
		});
	}

	//构建分页栏
	buildPageLabel = function() {
		$scope.pageLabel = [];
		var firstPage = 1;//开始页码
		var lastPage = $scope.resultMap.totalPages;//截止页码

		$scope.frontDot = true;//前面有点
		$scope.behindDot = true;//后面有点

		if ($scope.resultMap.totalPages > 5) {

			if ($scope.searchMap.pageNo <= 3) {
				lastPage = 5;
				$scope.frontDot = false;
			} else if($scope.searchMap.pageNo >= $scope.resultMap.totalPages -2) {
				firstPage = $scope.searchMap.pageNo - 4;
				$scope.behindDot = false;
			} else {
				firstPage = $scope.searchMap.pageNo - 2;
				lastPage = $scope.searchMap.pageNo + 2;
			}
		} else {
			$scope.frontDot = false;
			$scope.behindDot = false;
		}

		for (var i = firstPage; i <= lastPage; i++) {
			$scope.pageLabel.push(i);
		}
	}
	// 添加搜索项
	$scope.addSearchItem = function(key, value) {
		
		if(key == 'category' || key == 'brand' || key == 'price') { //如果用户点击的是分类或者品牌
			$scope.searchMap[key] = value;
		} else { //用户点击的是规格
			$scope.searchMap.spec[key] = value;
		}
		$scope.search();
	}
	// 撤销搜索项
	$scope.removeSearchItem = function(key) {
		if(key == 'category' || key == 'brand' || key == 'price') { //如果用户点击的是分类或者品牌
			$scope.searchMap[key] = "";
		} else { //用户点击的是规格
			delete $scope.searchMap.spec[key];
		}
		$scope.search();
	}
	//让搜索框响应回车键
	$scope.keyup = function(e) {
		var keycode = window.event?e.keyCode:e.which;
		if (keycode === 13) {
			$scope.search();
		}
	}
	//分页查询
	$scope.queryByPage = function(pageNo) {
		if (pageNo < 1 || pageNo > $scope.resultMap.totalPages) {
			return;
		}
		$scope.searchMap.pageNo = pageNo;
		$scope.search();//查询
	}

	//判断当前页是否为第一页
	$scope.isStartPage = function() {
		if ($scope.searchMap.pageNo == 1) {
			return true;
		} else {
			return false;
		}
	}

	//判断当前页是否为最后一页
	$scope.isEndPage = function() {
		if ($scope.searchMap.pageNo == $scope.resultMap.totalPages) {
			return true;
		} else {
			return false;
		}
	}

	//排序查询
	$scope.sortSearch = function(sortField, sortValue) {
		$scope.searchMap.sortField = sortField;
		$scope.searchMap.sortValue = sortValue;
		$scope.search();//查询
	}

	//判断关键字是否为品牌
	$scope.keywordsIsBrand = function() {
		for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
			if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text) >= 0) {
				return true;
			} 
		}
		return false;
	}

	//接收关键字参数
	$scope.loadKeywords = function() {
		$scope.searchMap.keywords = $location.search()['keywords'];
		$scope.search();
	}
});
