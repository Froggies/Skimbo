'use strict';

define(["app"], function(app) {

  app.directive("scrollmanager", ["$timeout", "$rootScope", "Visibility", "ArrayUtils", "Network",
    function($timeout, $rootScope, $visibility, $arrayUtils, $network) {
    
    var userScroll = false, scrollObjects = {}, timeoutsUpdateSinceId = {};

    function updateSinceId(referer, msg) {
      timeoutsUpdateSinceId[referer] = timeoutsUpdateSinceId[referer] || {};
      var uidProviderUser = msg.unifiedRequest.uidProviderUser;
      if(msg.unifiedRequest.args && msg.unifiedRequest.args[0]) {
        uidProviderUser + msg.unifiedRequest.args[0].value.call;//discriminant server side
      }
      if(timeoutsUpdateSinceId[referer][uidProviderUser] != undefined) {
        $timeout.cancel(timeoutsUpdateSinceId[referer][uidProviderUser]);
      }
      timeoutsUpdateSinceId[referer][uidProviderUser] = $timeout(function() {
        console.log(msg);
        $network.send({
          cmd: "updateSinceId",
          body: {
            columnTitle: msg.column,
            unifiedRequest: msg.unifiedRequest,
            sinceId: msg.sinceId
          }
        });
        timeoutsUpdateSinceId[referer][uidProviderUser] = undefined;
      }, 3000);
    }

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
              console.log(newValue);
              $rootScope.$on("scrollManagerGoTop", function(evt, column) {
                if(angular.equals(column.title, data)) {
                  userScroll = false;
                  scroller[0].scrollTop = 0;
                  console.log(scrollObjects[data]);
                  updateSinceId(data, scrollObjects[data][scrollObjects[data].length-1]);
                  $timeout(function() {
                    userScroll = true;
                  }, 100);
                }
              });
              var scrollMove = function(evt) {
                if(scrollObjects[data] && userScroll == true) {
                  for (var i = 0; i < scrollObjects[data].length; i++) {
                    var object = scrollObjects[data][i];
                    if(object.isView === false) {
                      var pos = object.refElement.offsetTop - scroller[0].offsetTop;
                      if(evt.target.scrollTop <= pos) {
                        scrollObjects[data].splice(i, 1);
                        object.isView = true;
                        $visibility.notifyMessageRead();
                        scope.$apply();
                        updateSinceId(data, object);
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