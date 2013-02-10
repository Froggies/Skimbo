'use strict';

define(["app"], function(app) {

  app.directive("scrollmanager", ["$timeout", "$rootScope", function($timeout, $rootScope) {
    
    var userScroll = false;
    
    return {
      restrict: 'A',
      link: function(scope, elmt, attrs)  {
        var element = elmt[0];
        if(attrs["scrollmanager"] === "onlyTop") {
          var scroller = angular.element(element); 
          $rootScope.$on("scrollManagerGoTop", function(evt, column) {
            if(angular.equals(column, scope[attrs["scrolldata"]])) {
              scroller[0].scrollTop = 0;
            }
          });
        } else {
          var scroller = angular.element(element.parentElement.parentElement); 
          var object = scope[attrs["scrollmanager"]];
          object.isView = false;
          var unwatch = scope.$watch(attrs["scrollmanager"], function(newValue, oldValue) {
            if(object.isView == false) {
              userScroll = false;
              scroller[0].scrollTop += element.clientHeight;
              $timeout(function() {
                userScroll = true;
              }, 0);
            }
          });
          
          var scrollMove = function(evt) {
            object.pos = element.offsetTop - scroller[0].offsetTop;
            if(evt.target.scrollTop <= object.pos && userScroll == true) {
              object.isView = true;
              scroller.unbind('scroll', scrollMove);
              unwatch();
              scope.$apply();
            }
          }
          scroller.bind('scroll', scrollMove);
        } 
      }
    };

  }]);

})