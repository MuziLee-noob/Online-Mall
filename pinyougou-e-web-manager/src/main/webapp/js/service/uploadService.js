app.service('uploadService', function($http) {

    //上传文件
    this.uploadFile = function() {

        var formdata = new FormData();
        formdata.append('file', file.files[0]);//第二个file是文件上传框的id
        return $http({
            url:'../upload.do',
            method:'post',
            data:formdata,
            headers:{'Content-Type':undefined},
            transformRequest: angular.identity //对文件进行二进制转化
        })
    }
});