app.controller("itemController", function($scope) {
	
	$scope.specificationItems={};//用来存储用户选择的规格

	//数量的加减
	$scope.addNum = function(x) {
		$scope.num += x;
		if ($scope.num < 1) {
			$scope.num = 1;
		}
	}
	
	//用户选择规格
	$scope.selectSpecification = function(key, value) {
		$scope.specificationItems[key] = value;
		searchSku();//查询sku
	}

	$scope.isSelected = function(key, value) {
		if ($scope.specificationItems[key] == value) {
			return true;
		} else {
			return false;
		}
	}

	$scope.sku = [];

	$scope.loadSku = function() {
		$scope.sku = skuList[0];
		$scope.specificationItems = JSON.parse(JSON.stringify($scope.sku.spec));
	}
	
	//匹配两个对象是否相等
	matchObject = function(map1, map2) {
		for (var k in map1) {
			if (map1[k] != map2[k]) {
				return false;
			}
		}
		for (var k in map2) {
			if (map2[k] != map1[k]) {
				return false;
			}
		}
		return true;
	}

	//查询SKU
	searchSku = function() {
		for (var i = 0; i < skuList.length; i++) {
			if (matchObject(skuList[i].spec, $scope.specificationItems)) {
				$scope.sku = skuList[i];
				return;
			}
		}
		$scope.sku={id:0, title:'------', price:0};//如果没有匹配的
	}

	$scope.addToCart = function() {
		alert('skuid:'+$scope.sku.id); 
	}
});