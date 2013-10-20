'use strict';

define(["app"], function(app) {

  app.directive('focus', [function() {

    return function (scope, element) {
      element[0].focus();
    };

  }]);
});
