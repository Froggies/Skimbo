'use strict';

define(["app"], function(app) {

  app.directive('showonhoverparent', [function() {

    return {
      link : function(scope, element, attrs) {
        element[0].hidden = true;
        element.parent().bind('mouseenter', function() {
          element[0].hidden = false;
        });
        element.parent().bind('mouseleave', function() {
          element[0].hidden = true;
        });
      }
    };

  }]);
});