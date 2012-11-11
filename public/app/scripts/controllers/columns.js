'use strict';

publicApp.controller('ColumnsCtrl', function($scope, $http) {
  var wshost = 'ws://127.0.0.1:9000/api/stream/webSocket';
  var ssehost = 'http://127.0.0.1:9000/api/stream/sse';
  var sseping = 'http://127.0.0.1:9000/api/stream/ping';

  if(window.MozWebSocket) {
    window.WebSocket=window.MozWebSocket;
  }
  if(!window.WebSocket) {
    //alert('Votre navigateur ne supporte pas les webSocket!');
    return false;
  } else {
    var socket = new WebSocket(wshost);
    socket.onopen = function() { 
      console.log('socket ouverte'); 
      var json = { 
        "channels": [
          { "service": "twitter.wall" }, 
          { "service": "twitter.hashtag", "args": { "hashtag": "skimbo" } },
          { "service": "twitter.user", "args": {"username": "studiodev"} }
        ]
      }
      json = {"cmd":"allColumns"}
      json = {
        "cmd":"addColumn", 
        "body":{
          "title":"title1", 
          "unifiedRequests":[
            {"service":"twitter.wall","args":{}},
            {"service":"twitter.user", "args":{"username":"RmManeschi"}},
            {"service":"twitter.hashtag", "args":{"hashtag":"skimbo"}},
            {"service":"facebook.wall","args":{}}
          ]
        }
      }
      //json = {"cmd":"allUnifiedRequests"}
      json = {"cmd":"delColumn", "body":{"title": "title3"}}
      socket.send(JSON.stringify(json));
    }
    socket.onclose = function() { console.log('socket fermée'); }
    socket.onerror = function() { console.log('Une erreur est survenue'); }
    socket.onmessage = function(msg){
      var data;
      try { //tente de parser data
        data = JSON.parse(msg.data);
      } catch(exception) {
        data = msg.data
      }      
      //ici on poura effectuer tout ce que l'on veux sur notre objet data
      console.log(data);
      $scope.$apply(function() {
        if($scope.data == undefined) {
            $scope.data = [];
          }
        $scope.data.unshift(data);
      });
    }
  } 

  if (!window.WebSocket && !!window.EventSource) {
    var source = new EventSource(ssehost);
    source.addEventListener('message', function(e) {
    	$scope.$apply(function() {
    		if($scope.data == undefined) {
        		$scope.data = [];
        	}
    		$scope.data.unshift(JSON.parse(e.data));
    	});
      console.log("ping");
      $http.get(sseping);
    }, false);

    source.addEventListener('open', function(e) {
      // Connection was opened.
    }, false);

    source.addEventListener('error', function(e) {
      if (e.readyState == EventSource.CLOSED) {
        // Connection was closed.
      }
    }, false);
  }

});


function dragOver(target, ev)
{
	ev.preventDefault();
}

function dragStart(target,ev)
{
	target.style.opacity = "0.5";
	ev.dataTransfer.setData("text/html", target.innerHTML);
	ev.dataTransfer.setData("id", target.id);
	ev.dataTransfer.effectAllowed = 'move';
}

function dragEnter(target, ev) {
	target.style.backgroundColor = "red";
	ev.preventDefault();
}

function dragLeave(target, ev) {
	target.style.backgroundColor = "pink";
	ev.preventDefault();
}

function dragEnd(target,ev)
{
	target.style.backgroundColor = "pink";
	target.style.opacity = "1";
}

function drop(target,ev)
{
	ev.preventDefault();
	var fromHtml=ev.dataTransfer.getData("text/html");
	var id=ev.dataTransfer.getData("id");

	var toHtml = target.innerHTML;

	target.innerHTML = fromHtml;
	document.getElementById(id).innerHTML = toHtml;

	target.style.backgroundColor = "pink";
	target.style.opacity = "1";
	// angular.element(target).scope().$root.$eval();
	var scope = angular.element(target).scope();
    // update the model with a wrap in $apply(fn) which will refresh the view for us
    // scope.$apply(function() {
    // 	console.log("test");
    //     scope.$eval(scope.data);
    // 	console.log("test apres");
    // }); 

}
