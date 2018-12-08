//购物车控制层  
app.controller('orderController', function ($scope, cartService) {
    //查询购物车列表 
    $scope.findOne = function () {
        cartService.findOne($scope.orderId).success(
            function (response) {
                $scope.order = response;
                $scope.totalValue = cartService.sum($scope.cartList);
            }
        );
    }

    //获取当前用户的地址列表
    $scope.findAddressList = function() {
        cartService.findAddressList().success(
            function(response) {
                $scope.addressList = response;
                for (var i = 0; i < $scope.addressList.length; i++){
                    if ($scope.addressList[i].isDefault == '1') {
                        $scope.address = $scope.addressList[i];
                        break;
                    }
                }
            }
        )
    }

    //选择地址
    $scope.selectAddress = function(address) {
        $scope.address = address;
    }

    //判断是否为选择地址
    $scope.isSelectedAddress = function(address) {
        if(address == $scope.address) {
            return true;
        } else {
            return false;
        }
    }

    //增加地址
    $scope.add = function() {
        cartService.addAddress($scope.address_add).success(
            function(response) {
                if (response.success) {
                    $scope.findAddressList();
                } else {
                    alert(response.message);
                }
            }            
        )
    }
    //提交订单
    $scope.submitOrder = function() {


    }
}); 