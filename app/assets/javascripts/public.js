'use strict';

// HACK Facebook : redirect to #_=_ and angular dont like this ...
if (document.location.hash == "#_=_") { document.location.replace("/"); }


var publicApp = angular.module('publicApp', ['ngSanitize', 'controllers', 'filters']);

publicApp.config(["$routeProvider", "$locationProvider", function($routeProvider, $locationProvider) {
	$routeProvider
    .when('/', {
      templateUrl: 'assets/app/views/main.html',
      controller: "ColumnController"
    })
    .otherwise({
      redirectTo: '/'
    });
  	$locationProvider.html5Mode(true);
}]);