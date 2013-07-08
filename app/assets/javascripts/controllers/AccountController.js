define(["app"], function(app) {

app.controller('AccountController', [
  "$scope", "$http", "DataCache", "Network",
  function($scope, $http, $dataCache, $network) {

    $dataCache.on('userInfos', function(data) {
      $scope.userInfos = data;
    });

    $scope.close = function() {
      $scope.$parent.hide();
    }

    $scope.selectImage = function(index) {
      var temp = $scope.userInfos[0];
      $scope.userInfos[0] = $scope.userInfos[index];
      $scope.userInfos[index] = temp;
      $dataCache.add('userInfos', $scope.userInfos);
    }

    $scope.deleteProvider = function(providerName) {
      $http.get("/api/providers/del/"+providerName).success(function() {
        for (var i = 0; i < $scope.userInfos.length; i++) {
          if($scope.userInfos[i].socialType == providerName) {
            $scope.userInfos.splice(i,1);
            $network.send({cmd:"allUnifiedRequests"});
            $network.send({cmd:"allPosters"});
            break;
          }
        }
      });
    }

}]);

});