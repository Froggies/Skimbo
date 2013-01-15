'use strict';

define(["app"], function(app) {

app.controller('HeaderController', [
  "$scope", "$rootScope", "$http", "ArrayUtils", "ImagesUtils", "Network",
  function($scope, $rootScope, $http, $arrayUtils, $imagesUtils, $network) {

  $scope.loading = true;
  $scope.loadingMsg = "COLUMNS";
  var firstMsg = true;

  $rootScope.$on('allColumns', function(evt, data) {
    if(data.length > 0 && data[0].unifiedRequests.length > 0) {
      $scope.$apply(function() {
        $scope.loadingMsg = "MESSAGES";
      });
    } else {
      $scope.$apply(function() {
        $scope.loading = false;
      });
    }
  });

  $rootScope.$on('msg', function(evt, data) {
    $scope.$apply(function() {
      if(firstMsg == true) {
        firstMsg = false;
        $scope.loading = false;
      }
    });
  });

  $rootScope.$on('loading', function(evt, data) {
    $scope.$apply(function() {
      $scope.loading = data.loading;
      if($scope.loading == true) {
        $scope.loadingMsg = data.translationCode;
      }
    });
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

	$scope.deleteProvider = function(providerName) {
      $http.get("/api/providers/del/"+providerName).success(function() {
        for (var i = 0; i < $scope.userInfos.length; i++) {
	        if($scope.userInfos[i].socialType == providerName) {
	          $scope.userInfos.splice(i,1);
            $network.send({cmd:"allUnifiedRequests"});
	          break;
	        }
      	}
      });
    }

}]);

return app;
});