'use strict';

define(["app"], function(app) {

app.controller('MainController', ['$scope', '$rootScope', function($scope, $rootScope) {

  // translation : 
  $rootScope.currentLanguage = navigator.language.substring(0,2);

}]);

return app;
});