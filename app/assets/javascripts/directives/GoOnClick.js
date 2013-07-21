'use strict';

define(["app"], function(app) {

  app.directive('goOnClick', [function() {

    return function (scope, element, attrs) {
      element.bind('click', function() {
      	document.getElementById(attrs.goOnClick).scrollIntoView();
      });
      element.bind('$destroy', function() {
      	element.unbind('click');
      	element.unbind('$destroy');
      })
    };

  }]);
});
