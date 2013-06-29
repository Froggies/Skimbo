'use strict';

define(["app"], function(app) {

app.controller('GlassController', ["$scope", "$rootScope", function($scope, $rootScope) {

  var _optionalCallback = undefined;
  $scope.showView = false;

  $rootScope.$on('glassShowView', function(evt, view, optionalCallback) {
    _optionalCallback = optionalCallback;
    if(view == 'modifColumn') {
      show('/assets/app/views/modifColumn.html');
    } else {
      show(view);
    }
  });

  function show(view) {
    if(view == $scope.showView) {
      $scope.hide();
    } else if(view == 'modifColumn') {
      $scope.showView = '/assets/app/views/modifColumn.html';
    } else {
      $scope.showView = view;
    }
  }

  $scope.hide = function() {
    $scope.showView = false;
  }

  $scope.loaded = function() {
    console.log("LOADED");
    if(_optionalCallback !== undefined) {
      _optionalCallback();
    }
  }

}]);

return app;
});