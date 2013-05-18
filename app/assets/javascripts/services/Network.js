'use strict';

define(["app"], function(app) {

app.factory("Network", ["$http", "$timeout", "ServerCommunication", "$location", '$window',
  function($http, $timeout, $serverCommunication, $location, $window) {

  var isSecure = $location.$$protocol === "https";
  var pageName = $location.path().split('/');

  var wshost = jsRoutes.controllers.stream.WebSocket.connect().webSocketURL(isSecure);

  if(pageName !== undefined && pageName.length == 3) {
    wshost = wshost + "/" + pageName[2];
  }

  console.log("WHOST URL = "+wshost);
  var ssehost = jsRoutes.controllers.stream.Sse.connect().absoluteURL(isSecure);
  console.log("SSEHOST URL = "+ ssehost);
  var sseping = jsRoutes.controllers.stream.Sse.ping().absoluteURL(isSecure);
  console.log("SSEPING URL = "+sseping);
  var longpolling = jsRoutes.controllers.stream.LongPolling.connect().absoluteURL(isSecure);
  console.log("LONGPOLLING URL = "+longpolling);

  var socket = undefined;//ws mode
  var source = undefined;//sse mode
  var nbError = 0;
  var nbErrorMax = 5;
  var dataReceivedByWS = false;

  console.log("ws supported", "WebSocket" in window);
  console.log("sse supported", "EventSource" in window);

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

  function longPollingMode() {
    console.log("longPollingMode actif !");
    var fakeIframe = document.createElement('iframe');
    fakeIframe.hidden = true;
    fakeIframe.src = longpolling;
    window.longPolling = function(msg) {
      var data;
      try {
          data = JSON.parse(msg);
      } catch(exception) {
          data = msg;
      }      
      command(data);
    }
    console.log(angular.element($window));
    $timeout(function() {
      document.body.appendChild(fakeIframe);
    }, 3500);
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
  if(!window.WebSocket && !window.EventSource) {
    longPollingMode();
  } else /*if(!window.WebSocket)*/ {
    sseMode();
  } /*else {
    webSocketMode();
  } */

  return {

    send: function(jsonMsg) {
      _send(jsonMsg);
    }


  };

}]);

return app;
});