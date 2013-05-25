define(["app"], function(app) {

app.controller('AccountController', [
  "$scope", "$rootScope", "$http", "ArrayUtils", "ImagesUtils", "Network",
  function($scope, $rootScope, $http, $arrayUtils, $imagesUtils, $network) {

    $scope.showUserInformations = false;

    $rootScope.$on('displayViewMenu', function(evt, view) {
      if(view !== 'account') {
        $scope.showUserInformations = false;
      }
    });

    $rootScope.$on('userInfos', function(evt, data) {
      $scope.$apply(function() {
        if($scope.userInfos == undefined) {
          $scope.userInfos = [];
          $scope.userInfos.push(data);
        } else {
          var index = $arrayUtils.indexOf($scope.userInfos, data, "socialType");
          if(index > -1) {
            $scope.userInfos[index] = data;
          } else {
            if($imagesUtils.isDefaultImage($scope.userInfos[0].avatar)) {
              $scope.userInfos.splice(0, 0, data);
            } else {
              $scope.userInfos.push(data);
            }
          }
        }
      });
    });

    $scope.show = function() {
      $scope.showUserInformations = !$scope.showUserInformations;
      if ($scope.showUserInformations == true) {
        $rootScope.$broadcast('displayViewMenu', 'account');
      }
    };

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