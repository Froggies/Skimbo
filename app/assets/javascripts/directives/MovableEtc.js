'use strict';

define(["app"], function(app) {

  app.directive('movableEtc', ['$timeout', function($timeout) {

    function addDot(element) {
      if(element[0].innerText.length < 3) {
        element[0].innerText = element[0].innerText + ".";
      } else {
        element[0].innerText = ".";
      }
      $timeout(function() {
        addDot(element);
      }, 500);
    }

    return {
      link : function(scope, element, attrs) {
        addDot(element);
      }
    };

  }]);
});