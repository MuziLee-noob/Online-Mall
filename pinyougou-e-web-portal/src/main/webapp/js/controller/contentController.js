app.controller('contentController', function($scope, contentService){

    $scope.contentList = [];//广告列表

    $scope.findByCategoryId = function(categoryId) {
        contentService.findByCategoryId(categoryId).success(
            function(response){
                $scope.contentList[categoryId] = response;
            }
        );
    }

    //搜索（传递参数）
    $scope.search = function() {
        location.href = "http://localhost:9104/search.html#?keywords=" + $scope.keywords;
    }

    //让搜索框响应回车键
	$scope.keyup = function(e) { 
		var keycode = window.event?e.keyCode:e.which;
		if (keycode === 13) {
			$scope.search();
		}
	}
});