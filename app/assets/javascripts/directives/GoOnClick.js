'use strict';

define(["app"], function(app) {

  app.directive('goOnClick', ['$rootScope', function($rootScope) {

    $rootScope.$on('goOnClick', function(evt, idElement) {
      go(idElement);
    });

    function go(idElement) {
      document.getElementById(idElement).scrollIntoView();
    }

    return function (scope, element, attrs) {
      element.bind('click', function() {
      	go(attrs.goOnClick);
      });
      element.bind('$destroy', function() {
      	element.unbind('click');
      	element.unbind('$destroy');
      })
    };

  }]);
});
