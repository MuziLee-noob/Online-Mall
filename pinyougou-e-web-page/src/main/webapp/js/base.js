var app = angular.module('pinyougou', []);
	// 定义过滤器
	app.filter('trustHtml', ['$sce', function($sce) {
		//data表示被过滤的内容
		return function(data) {
			return $sce.trustAsHtml(data);//返回过滤后的内容，信任data中的html内容
		}
	}]);
