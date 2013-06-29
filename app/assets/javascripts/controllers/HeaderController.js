'use strict';

define(["app"], function(app) {

app.controller('HeaderController', [
  "$scope", "$rootScope", "DataCache",
  function($scope, $rootScope, $dataCache) {

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
    $scope.loading = data.loading;
    if($scope.loading == true) {
      $scope.loadingMsg = data.translationCode;
    }
  });

  $dataCache.on('userInfos', function(data) {
    $scope.userInfos = data;
  });

  $scope.showAccount = function() {
    $rootScope.$broadcast('glassShowView', '/assets/app/views/account.html');
  }

  $scope.showSkimber = function() {
    $rootScope.$broadcast('glassShowView', '/assets/app/views/post.html');
  }

  $scope.showModifColumn = function() {
    $rootScope.$broadcast('glassShowView', '/assets/app/views/modifColumn.html');
  }

}]);

return app;
});