'use strict';

// HACK Facebook : redirect to #_=_ and angular dont like this ...
if (document.location.hash == "#_=_") { document.location.replace("/"); }

define(['angular'], function(angular) {
  var publicApp = angular.module('publicApp', ['ngSanitize']);
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
  return publicApp;
});