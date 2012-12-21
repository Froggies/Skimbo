'use strict';

publicApp.controller('MainController', function($scope, $rootScope) {

	$scope.showUserInformations = false;
  // translation : 
  $rootScope.currentLanguage = navigator.language.substring(0,2);
  console.log(navigator.language);

});