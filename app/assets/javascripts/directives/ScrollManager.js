'use strict';

define(["app"], function(app) {

  app.directive("scrollmanager", ["$timeout", "$rootScope", "Visibility", 
    function($timeout, $rootScope, $visibility) {
    
    var userScroll = false;

    var scrollObjects = {};

    return {
      restrict: 'A',
      link: function(scope, elmt, attrs)  {
        var element = elmt[0];
        var timeout = undefined;
        if(attrs["scrollmanager"] === "onlyTop") {
          var scroller = angular.element(element);
          var unwatch = scope.$watch(attrs["scrolldata"], function(newValue, oldValue) {
            if(newValue) {
              var data = newValue.title;
              console.log(data);
              $rootScope.$on("scrollManagerGoTop", function(evt, column) {
                if(angular.equals(column.title, data)) {
                  userScroll = false;
                  scroller[0].scrollTop = 0;
                  $timeout(function() {
                    userScroll = true;
                  }, 100);
                }
              });
              var scrollMove = function(evt) {
                if(scrollObjects[data] && userScroll == true) {
                  for (var i = scrollObjects[data].length-1; i >= 0; i--) {
                    var object = scrollObjects[data][i];
                    if(object.isView == false) {
                      object.pos = object.scrollElement.offsetTop - scroller[0].offsetTop;
                      if(evt.target.scrollTop <= object.pos) {
                        scrollObjects[data].splice(i, 1);
                        object.isView = true;
                        $visibility.notifyMessageRead();
                        scope.$apply();
                        return;
                      }
                    }
                  }
                }
              }
              scroller.bind('scroll', scrollMove);
              unwatch();
            }
          });
        } else {
          var scroller = angular.element(element.parentElement.parentElement);
          var referer = scope[attrs["scrolldata"]].title;
          var object = scope[attrs["scrollmanager"]];
          object.scrollElement = element;
          scrollObjects[referer] = scrollObjects[referer] || [];
          scrollObjects[referer].push(object);
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
              unwatch();
            }
          });
        } 
      }
    };

  }]);

})