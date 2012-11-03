'use strict';

publicApp.controller('MainCtrl', function($scope, $http) {

  $scope.logout = function() {
  	console.log("logout");
    $http.get("http://127.0.0.1:9000/api/logout");
  }

});