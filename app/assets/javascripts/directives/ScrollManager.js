'use strict';

define(["app"], function(app) {

  app.directive("scrollmanager", ["$timeout", "$rootScope", "Visibility", "ArrayUtils",
    function($timeout, $rootScope, $visibility, $arrayUtils) {
    
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
                  for (var i = 0; i < scrollObjects[data].length; i++) {
                    var object = scrollObjects[data][i];
                    if(object.isView == false) {
                      var pos = object.refElement.offsetTop - scroller[0].offsetTop;
                      if(evt.target.scrollTop <= pos) {
                        scrollObjects[data].splice(i, 1);
                        object.isView = true;
                        $visibility.notifyMessageRead();
                        scope.$apply();
                      } else {
                        //when object post is above scroll we stop
                        //why the fuck insert array isn't ordered ?????????
                        //return;
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
          var unwatch = scope.$watch(attrs["scrollmanager"], function(newValue, oldValue) {
            if(newValue) {
              var object = scope[attrs["scrollmanager"]];
              if(object.isView === false) {
                var referer = scope[attrs["scrolldata"]].title;
                object.refElement = element;
                scrollObjects[referer] = scrollObjects[referer] || [];
                object.isView = false;
                userScroll = false;
                scroller[0].scrollTop += element.clientHeight;
                if(timeout != undefined) {
                  $timeout.cancel(timeout);
                }
                timeout = $timeout(function() {
                  userScroll = true;
                  timeout = undefined;
                }, 500);
                scrollObjects[referer].splice(
                  $arrayUtils.sortedArrayLocationOf(object, scrollObjects[referer], 'createdAt')+1, 
                  0, 
                  object);
              }
              unwatch();
            }
          });
        } 
      }
    };

  }]);

})