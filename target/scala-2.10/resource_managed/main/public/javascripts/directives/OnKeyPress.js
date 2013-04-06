'use strict';

define(["app"], function(app) {
  app.directive('onKeyPress', function() {
    return {
      restrict : 'A',
      link : function($scope,$element,$attr) {
        document.onkeypress = function(e) {
          e = e || window.event;
          var charCode = (typeof e.which == "number") ? e.which : e.keyCode;
          if (charCode) {
            //console.log("Character typed: " + charCode);
            if(e.ctrlKey && e.charCode == $attr.onKeyPress) {
              $scope.$eval($attr.onKeyPressExec);
              e.stopPropagation();
              e.preventDefault();
            }
          }
        }
      }
    }
  });
});