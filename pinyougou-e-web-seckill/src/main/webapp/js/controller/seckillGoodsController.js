app.controller('seckillGoodsController', function ($scope, seckillGoodsService, $interval, $location) {
    //读取列表数据绑定到表单中   
    $scope.findList = function () {
        seckillGoodsService.findList().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //查询实体  
    $scope.findOne = function () {
        seckillGoodsService.findOne($location.search()['id']).success(
            function (response) {
                $scope.entity = response;

                allsecond = Math.floor((new Date($scope.entity.endTime).getTime() - new Date().getTime()) / 1000);

                time = $interval(function () {
                    allsecond = allsecond - 1;                 
                    if (allsecond > 0) {
                        allsecond = allsecond - 1;
                        $scope.timeString=convertTimeString(allsecond);//转换时间字符
                    } else {
                        $interval.cancel(time);
                        alert("秒杀服务已结束");
                    }
                }, 1000);
            }
        );
    }

    //日期格式转换 second --> dd : HH : ss
    convertTimeString = function (allsecond) {
        var days = Math.floor(allsecond / (60 * 60 * 24));
        var hours = Math.floor((allsecond - days * 60 * 60 * 24) / (60 * 60));
        var minutes = Math.floor((allsecond - days * 60 * 60 * 24 - hours * (60 * 60)) / 60);
        var seconds = Math.floor(allsecond - days * 60 * 60 * 24 - hours * (60 * 60) - minutes * 60);
        if (days > 0) {
            timeString = days + "天";
        }
        return timeString + hours + ":" + minutes + ":" + seconds
    }


    $scope.submitOrder = function() {
        seckillGoodsService.submitOrder($scope.entity.id).success(
            function(response) {
                if (response.success) {
                    alert("下单成功，请在 5 分钟内完成支付"); 
                    location.href = "pay.html";//跳转支付页面
                } else {
                    alert(response.message);
                }
            }
        );
    }
});  