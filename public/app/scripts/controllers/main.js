'use strict';

publicApp.controller('MainCtrl', function($scope, $http) {

  $scope.logout = function() {
  	console.log("killMyActors");
    $http.get("http://127.0.0.1:9000/api/killMyActors");
  }

});