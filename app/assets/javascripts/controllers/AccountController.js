define(["app"], function(app) {

app.controller('AccountController', [
  "$scope", "$http", "DataCache", "Network",
  function($scope, $http, $dataCache, $network) {

    $dataCache.on('userInfos', function(data) {
      $scope.userInfos = data;
    });

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