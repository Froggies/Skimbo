'use strict';

define(["app"], function(app) {

  app.directive('movableEtc', ['$timeout', '$filter', function($timeout, $filter) {

    var retreiveMessage = $filter('i18n')('RETRIEVING_MESSAGES');
    var scrollToShowMessage = $filter('i18n')('SCROLL_TO_SHOW_MSG');

    return {
      link : function(scope, element, attrs) {
        var dot = '';

        function addDot(element) {
          if(dot !== undefined) {
            if(dot.length < 3) {
              dot += '.';
            } else {
              dot = ".";
            }
            element[0].innerText = retreiveMessage + dot;
            $timeout(function() {
              addDot(element);
            }, 500);
          }
        };
        addDot(element);

        var unwatch = scope.$watch(attrs["movableEtc"]+".messages", function(newVal, oldVal) {
          if(newVal) {
            dot = undefined;//stop dot recursion
            $timeout(function() {
              element[0].innerText = scrollToShowMessage;
            }, 500);
            unwatch();
          } else {
            element[0].innerText = retreiveMessage;
          }
        });
        element.bind("$destroy", function() {
          unwatch();
        });
      }
    };

  }]);
});