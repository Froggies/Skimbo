'use strict';

define(["app"], function(app) {

app.factory("Network", ["$http", "ServerCommunication", function($http, $serverCommunication) {

  var wshost = jsRoutes.controllers.stream.WebSocket.connect().webSocketURL();
  var ssehost = jsRoutes.controllers.stream.Sse.connect().absoluteURL();
  var sseping = jsRoutes.controllers.stream.Sse.ping().absoluteURL();

  function webSocketMode() {
    socket = new WebSocket(wshost);
    socket.onclose = function() { 
      socket = undefined; 
      sseMode(); 
      console.log('WS socket fermée'); 
    };
    socket.onerror = function() { 
      socket = undefined; 
      sseMode(); 
      console.log('WS Une erreur est survenue'); 
    };
    socket.onmessage = function(msg){
      var data;
      try {
          data = JSON.parse(msg.data);
      } catch(exception) {
          data = msg.data;
      }     
      //console.log(data); 
      command(data);
    }
  }

  function sseMode() {
    var source = new EventSource(ssehost);
    source.addEventListener('message', function(msg) {
      var data;
      try {
          data = JSON.parse(msg.data);
      } catch(exception) {
          data = msg.data;
      }      
      command(data);
    }, false);

    source.addEventListener('error', function(e) {
      source.close(); 
      console.log('sse Une erreur est survenue');
    }, false);
  }

  function command(data) {
    if(data.cmd == "ping") {
      //only sse connexion
      _send({"cmd":"pong"});
    } else {
      // console.log(data);
      $serverCommunication.cmd(data);
    }
  }

  function _send(jsonMsg) {
    //console.log("send : ",jsonMsg);
    if(socket !== undefined) {
      socket.send(JSON.stringify(jsonMsg));
    } else {
      $http.post('/api/stream/command', JSON.stringify(jsonMsg));
    }
  }
  
  var socket;
  if(window.MozWebSocket) {
    window.WebSocket = window.MozWebSocket;
  }
  if(!window.WebSocket) {
    sseMode();
  } else {
    webSocketMode();
  } 

  return {

    send: function(jsonMsg) {
      _send(jsonMsg);
    }


  };

}]);

return app;
});