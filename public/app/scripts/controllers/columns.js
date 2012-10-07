'use strict';

publicApp.controller('ColumnsCtrl', function($scope, $http) {
  $http.get('http://127.0.0.1:9000/ex/trello')
  	.success(function(json) {
	      $scope.data = json;
  	});
	
});
