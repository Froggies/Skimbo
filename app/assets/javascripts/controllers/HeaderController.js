'use strict';

publicApp.controller('HeaderController', function($scope, $rootScope, $http) {

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

});