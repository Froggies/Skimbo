'use strict';

define(["app"], function(app) {

  app.directive('oracle', ['Network', '$rootScope', '$timeout', function($network, $rootScope, $timeout) {

    var service, element, scope, lastSend, lastTimeout = undefined;

    $rootScope.$on('paramHelperSearch', function(evt, objRes) {
      if(service !== undefined && element != undefined && service.service == objRes.serviceName) {
        console.log(objRes);
        scope.$apply(function() {
          scope.arg.possibleValues = objRes.values;
        });
      }
    });

    function send(serviceName, search) {
      if(search != lastSend) {
        $network.send({
          cmd: "paramHelperSearch",
          body: {
            serviceName: serviceName,
            search: search
          }
        });
      }
      lastSend = search;
    }

    return {
      link : function(sc, elmt, attrs) {
        scope = sc;
        console.log(scope);
        service = scope[attrs["oracle"]];
        element = elmt;
        if(service.hasHelper == true) {
          element.bind('keyup', function() {
            if(lastTimeout != undefined) {
              $timeout.cancel(lastTimeout);
            }
            lastTimeout = $timeout(function() {
              if(element[0].value !== "") {
                send(service.service, element[0].value);
              } else {
                scope.arg.possibleValues = [];
              }
            }, 500);
          });
        }
      }
    };

  }]);
});