//购物车控制层  
app.controller('cartController', function ($scope, cartService) {
    //查询购物车列表 
    $scope.findCartList = function () {
        cartService.findCartList().success(
            function (response) {
                $scope.cartList = response;
                $scope.totalValue = cartService.sum($scope.cartList);
            }
        );
    }
    //添加商品到购物车 
    $scope.addGoodsToCartList = function (itemId, num) {
        cartService.addGoodsToCartList(itemId, num).success(
            function(response) {
                if (response.success) {
                    $scope.findCartList();
                } else {
                    alert(respopnse.message);
                }
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
    //选择支付类型
    $scope.order = {paymentType:'1'};
    $scope.selectPayType = function(type) {
        $scope.order.paymentType = type;
    }
    //判断是否选中
    $scope.isSelectedPayType = function(type) {
        if(type ==  $scope.order.paymentType) {
            return true;
        } else {
            return false;
        }
    }

    //提交订单
    $scope.submitOrder = function() {

        $scope.order.receiverAreaName = $scope.address.address;
        $scope.order.receiverMobile = $scope.address.mobile;
        $scope.order.receiver = $scope.address.contact;

        cartService.submitOrder($scope.order).success(
            function(response) {
                if (response.success) {
                    if ($scope.order.paymentType == '1') {
                        location.href='pay.html';
                    } else {
                        location.href='paysuccess.html';
                    }
                } else {
                    alert(response.message);
                }  
            }
        )
    }
}); 