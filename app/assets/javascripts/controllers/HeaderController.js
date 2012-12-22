'use strict';

publicApp.controller('HeaderController', [
  "$scope", "$rootScope", "$http", function($scope, $rootScope, $http) {

  $scope.loading = true;
  $scope.loadingMsg = "COLUMNS";
  var firstMsg = true;

  $rootScope.$on('allColumns', function(evt, data) {
    $scope.$apply(function() {
      $scope.loadingMsg = "MESSAGES";
    });
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
      }
      var found = false;
      for (var i = 0; i < $scope.userInfos.length && !found; i++) {
        var userInfos = $scope.userInfos[i];
        if(userInfos.socialType == data.socialType) {
          found = true;
          $scope.userInfos[i] = data;
        }
      };
      if(!found) {
        $scope.userInfos.push(data);
      }
    });
  });

	$scope.deleteProvider = function(providerName) {
      $http.get("/api/providers/del/"+providerName).success(function() {
        for (var i = 0; i < $scope.userInfos.length; i++) {
	        if($scope.userInfos[i].socialType == providerName) {
	          $scope.userInfos.splice(i,1);
	          break;
	        }
      	}
      });
    }

}]);