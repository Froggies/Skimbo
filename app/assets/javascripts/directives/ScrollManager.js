'use strict';

define(["app"], function(app) {

  app.directive("scrollmanager", ["$timeout", "$rootScope", "Visibility", 
    function($timeout, $rootScope, $visibility) {
    
    var userScroll = false;
    
    return {
      restrict: 'A',
      link: function(scope, elmt, attrs)  {
        var element = elmt[0];
        var timeout = undefined;
        if(attrs["scrollmanager"] === "onlyTop") {
          var scroller = angular.element(element); 
          $rootScope.$on("scrollManagerGoTop", function(evt, column) {
            if(angular.equals(column, scope[attrs["scrolldata"]])) {
              userScroll = false;
              scroller[0].scrollTop = 0;
              $timeout(function() {
                userScroll = true;
              }, 100);
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
              if(timeout != undefined) {
                $timeout.cancel(timeout);
              }
              timeout = $timeout(function() {
                userScroll = true;
                timeout = undefined;
              }, 500);
            }
          });
          
          var scrollMove = function(evt) {
            object.pos = element.offsetTop - scroller[0].offsetTop;
            if(evt.target.scrollTop <= object.pos && userScroll == true) {
              object.isView = true;
              $visibility.notifyMessageRead();
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