(function () {
  'use strict';

  // HACK Facebook : redirect to #_=_ and angular dont like this ...
  if (document.location.hash == "#_=_") { document.location.replace("/"); }



  var publicApp = angular.module('publicApp', ['ngSanitize', 'templates-app']);



  publicApp.config(["$routeProvider", "$locationProvider",
    function($routeProvider, $locationProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'views/main.html',
        controller: "ColumnController"
      })
      .when('/p/:pageName', {
        templateUrl: 'views/main.html',
        controller: "ColumnController"
      })
      .otherwise({
        redirectTo: '/'
      });
      //$locationProvider.html5Mode(true);
  }]);

  window.onload = function() {
    angular.bootstrap(window.document.getElementById('publicApp'), ['publicApp']);
  };



})();