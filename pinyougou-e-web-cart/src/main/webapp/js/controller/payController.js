app.controller('payController', function ($scope, payService, $location) {
    //本地生成二维码 
    $scope.createNative = function () {
        payService.createNative().success(
            function (response) {
                $scope.total_fee = (response.total_fee / 100).toFixed(2); //金额 
                $scope.out_trade_no = response.out_trade_no;//订单号 
                //二维码 
                var qr = new QRious({
                    element: document.getElementById('qrious'),
                    size: 250,
                    level: 'H',
                    value: response.code_url
                });
                queryPayStatus(response.out_trade_no);
            }
        );
    }

    //查询支付状态  
    queryPayStatus = function (out_trade_no) {
        payService.queryPayStatus(out_trade_no).success(
            function (response) {
                if (response.success) {
                    if (response.message == "待支付") {
                        document.getElementById('qrious').innerHTML("已扫码，请支付");
                    } else {
                        location.href = "paysuccess.html#?money=" + $scope.total_fee;
                    }
                } else {
                    if (response.message == "二维码超时") {
                        $scope.createNative();//重新生成二维码
                    } else {
                        location.href = "payfail.html";
                    }
                  
                }
            }
        );
    }
  
    $scope.getMoney = function() {
        return $location.search()['money'];
    }
}); 