app.controller('baseController', function($scope) {
	
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
	};
	
	$scope.selectIds = [];// 用户勾选的id集合

	$scope.updateSelection = function($event, id) {
		if ($event.target.checked) {
			$scope.selectIds.push(id);// push向集合添加元素
		} else {
			var index = $scope.selectIds.indexOf(id);// 查找值的位置
			$scope.selectIds.splice(index, 1);
		}
	}

	//将json转换为字符串
	$scope.jsonToString = function(jsonString, key) {
		var json = JSON.parse(jsonString);
		var value = "";
		value += json[0][key];
		for (var i = 1; i < json.length; i++) {
			value += "," + json[i][key];
		}
		return value;
	}

	//根据key的值查询集合中的某个对象
	$scope.searchObjectByKey = function(list, key, keyValue) {
		for (var i = 0; i < list.length; i++) {
			if (list[i][key] == keyValue) {
				return list[i];
			}
		}
		return null;
	}
});