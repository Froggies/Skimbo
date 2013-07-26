'use strict';

define(["app"], function(app) {

app.factory("Network", ["$http", "$timeout", "ServerCommunication", "$location", '$window',
  function($http, $timeout, $serverCommunication, $location, $window) {

  function _send(jsonMsg) {
    
  }
  
  return {

    send: function(jsonMsg) {
      _send(jsonMsg);
    }


  };

}]);

return app;
});