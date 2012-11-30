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
});