'use strict';

define(["app"], function(app) {

  app.directive('oracle', ['Network', '$rootScope', '$timeout', 
    function($network, $rootScope, $timeout) {

    var lastSend, lastTimeout = undefined;

    function initListen(type, service, element, scope, putResIn) {
      $rootScope.$on(type, function(evt, objRes) {
        if(service !== undefined && element != undefined && service.service == objRes.serviceName) {
          scope.$apply(function() {
            putResIn.possibleValues = objRes.values;
            $rootScope.$broadcast('loading', {loading: false, translationCode: 'PARAM_HELPER_SEARCH'});
          });
        }
      });
    }

    function send(type, serviceName, search) {
      if(search != lastSend) {
        var s = {
          cmd: type,
          body: {
            serviceName: serviceName,
            search: search
          }
        };
        $network.send(s);
        $rootScope.$broadcast('loading', {loading: true, translationCode: 'PARAM_HELPER_SEARCH'});
      }
      lastSend = search;
    }

    return {
      link : function(scope, element, attrs) {
        var service = scope[attrs["oracle"]];
        if(service.hasHelper == true || service.canHavePageId == true) {
          var type = attrs["oracleType"];
          var putResIn = scope[attrs["oracleStore"]];
          initListen(type, service, element, scope, putResIn);
          element.bind('keyup', function() {
            if(lastTimeout != undefined) {
              $timeout.cancel(lastTimeout);
            }
            lastTimeout = $timeout(function() {
              console.log(service, element[0].value);
              if(element[0].value !== "") {
                send(type, service.service, element[0].value);
              } else {
                putResIn.possibleValues = [];
              }
            }, 500);
          });
          element.bind("$destroy", function() {
            element.unbind('keyup');
          });
        }
      }
    };

  }]);
});