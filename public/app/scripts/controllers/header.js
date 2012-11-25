'use strict';

publicApp.controller('HeaderCtrl', function($scope, $http) {

	$scope.deleteProvider = function(providerName) {
      $http.get("/api/providers/del/"+providerName).success(function() {
        for (var i = 0; i < $scope.$parent.$parent.userInfos.length; i++) {
	        if($scope.$parent.$parent.userInfos[i].socialType == providerName) {
	          $scope.$parent.$parent.userInfos.splice(i,1);
	          break;
	        }
      	}
      });
    }

});