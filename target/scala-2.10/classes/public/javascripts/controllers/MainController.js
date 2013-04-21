'use strict';

define(["app"], function(app) {

app.controller('MainController', ['$scope', '$rootScope', function($scope, $rootScope) {

	$scope.showUserInformations = false;
  // translation : 
  $rootScope.currentLanguage = navigator.language.substring(0,2);
  console.log(navigator.language);

}]);

return app;
});