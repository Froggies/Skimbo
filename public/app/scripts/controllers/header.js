'use strict';

publicApp.controller('HeaderCtrl', function($scope, $http) {

	$scope.deleteProvider = function(providerName) {
      var cmd = JSON.stringify({"cmd":"deleteProvider", "body":{"provider": providerName}});
      $http.post("/api/providers/delete", cmd).success(function() {
        for (var i = 0; i < $scope.$parent.$parent.userInfos.length; i++) {
	        if($scope.$parent.$parent.userInfos[i].socialType == providerName) {
	          $scope.$parent.$parent.userInfos.splice(i,1);
	          break;
	        }
      	}
      });
    }

});