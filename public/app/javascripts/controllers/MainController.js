(function () {

	'use strict';

	angular.module('publicApp').controller('MainController', [
	'$scope', '$rootScope', 'Network',
	function($scope, $rootScope, $network) {

	  // translation : 
	  $rootScope.currentLanguage = navigator.language.substring(0,2);

	  $network.send({cmd:"allUnifiedRequests"});

	}]);

})();