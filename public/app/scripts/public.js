'use strict';

// HACK Facebook : redirect to #_=_ and angular dont like this ...
if (document.location.hash == "#_=_") { document.location.replace("/"); }

// Declare app level module which depends on filters, and services
var publicApp = angular.module('publicApp', ['ngSanitize'], function($routeProvider, $locationProvider) {
  $routeProvider
    .when('/', {
      templateUrl: 'assets/app/views/main.html'
    })
    .otherwise({
      redirectTo: '/'
    });
  $locationProvider.html5Mode(true);
})

//TO ADD ON ROOT SCOPE
/*.run(function($rootScope) {
  $rootScope.logout = function() {
    console.log('hello');
    $http.get("http://127.0.0.1:9000/api/killMyActors");
  }
});*/

