'use strict';

publicApp.controller('MainCtrl', function($scope, $http) {

  $scope.logout = function() {
    $http.get("http://127.0.0.1:9000/api/killMyActors");
  }

});
