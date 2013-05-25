'use strict';

define(["app"], function(app) {

app.controller('HeaderController', [
  "$scope", "$rootScope",
  function($scope, $rootScope) {

  $scope.loading = true;
  $scope.loadingMsg = "COLUMNS";
  $scope.banner_classes = "banner-close";
  $scope.arrow_classes = "arrow-open icon-fast-forward";
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

  $scope.openMenu = function() {
    if($scope.arrow_classes == "arrow-close icon-fast-backward") {
      $scope.arrow_classes = "arrow-open icon-fast-forward";
      $scope.banner_classes = "banner-close";
    } else {
      $scope.arrow_classes = "arrow-close icon-fast-backward";
      $scope.banner_classes = "banner-open";
    }
  }

}]);

return app;
});