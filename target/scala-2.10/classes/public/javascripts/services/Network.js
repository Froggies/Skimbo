'use strict';

define(["app"], function(app) {

app.factory("Network", ["$http", "$timeout", "ServerCommunication", 
  function($http, $timeout, $serverCommunication) {

  var wshost = jsRoutes.controllers.stream.WebSocket.connect().webSocketURL();
  console.log("WHOST URL = "+wshost);
  var ssehost = jsRoutes.controllers.stream.Sse.connect().url;
  console.log("SSEHOST URL = "+ ssehost);
  var sseping = jsRoutes.controllers.stream.Sse.ping().url;
  console.log("SSEPING URL = "+sseping);

  var socket = undefined;//ws mode
  var source = undefined;//sse mode
  var nbError = 0;
  var nbErrorMax = 5;
  var dataReceivedByWS = false;

  function webSocketMode() {
    if(socket == undefined) {
      socket = new WebSocket(wshost);
      socket.onclose = function() { 
        socket = undefined; 
        nbError++;
        $timeout(function() {
          if(dataReceivedByWS == true && nbError < nbErrorMax) {
            webSocketMode();
          } else {
            nbError = 0;
            sseMode();
          } 
        }, 500);
        console.log('WS closed : nbError = ', nbError); 
      };
      socket.onerror = function() { 
        socket = undefined; 
        nbError++;
        $timeout(function() {
          if(dataReceivedByWS == true && nbError < nbErrorMax) {
            webSocketMode();
          } else {
            nbError = 0;
            sseMode();
          } 
        }, 800);
        console.log('WS error : nbError = ', nbError); 
      };
      socket.onmessage = function(msg){
        dataReceivedByWS = true;
        nbError = 0;
        var data;
        try {
            data = JSON.parse(msg.data);
        } catch(exception) {
            data = msg.data;
        }     
        //console.log(data); 
        command(data);
      }
      $timeout(function() {
        if(dataReceivedByWS == false && source == undefined) {
          console.log("no data after 5sc !");
          //chrome doesn't fire error on proxy network !
          socket = undefined;
          nbError = 0;
          sseMode();
        }
      }, 5000);
    }
  }

  function sseMode() {
    console.log("sseMode actif !");
    if(source == undefined) {
      source = new EventSource(ssehost);
      source.addEventListener('message', function(msg) {
        nbError = 0;
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
        source = undefined;
        nbError++;
        $timeout(function() {
          if(nbError < nbErrorMax) {
            sseMode();
          } else {
            $serverCommunication.cmd({'cmd':'disconnect'});
          }
        }, 800);
        console.log('sse error : nb = ', nbError);
      }, false);
    }
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