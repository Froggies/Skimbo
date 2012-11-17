'use strict';

publicApp.controller('ColumnsCtrl', function($scope, $http) {
    var wshost = 'ws://127.0.0.1:9000/api/stream/webSocket';
    var ssehost = 'http://127.0.0.1:9000/api/stream/sse';
    var sseping = 'http://127.0.0.1:9000/api/stream/ping';
    
    var socket;
    if(window.MozWebSocket) {
        window.WebSocket=window.MozWebSocket;
    }
    if(!window.WebSocket) {
        alert('Votre navigateur ne supporte pas les webSocket !');
        return false;
    } else {
        socket = new WebSocket(wshost);
        socket.onopen = function() { console.log('socket ouverte'); }
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
            executeCommand(socket, data);
        }
    } 

    if (!window.WebSocket && !!window.EventSource) {
        var source = new EventSource(ssehost);
        source.addEventListener('message', function(msg) {
            var data;
            try { //tente de parser data
                data = JSON.parse(msg.data);
            } catch(exception) {
                data = msg.data
            }      
            //ici on poura effectuer tout ce que l'on veux sur notre objet data
            executeCommand(socket, data);
            console.log("ping");
            $http.get(sseping);
        }, false);

        source.addEventListener('open', function(e) {console.log('socket ouverte');}, false);
        source.addEventListener('error', function(e) {console.log('Une erreur est survenue');}, false);
    }

    $scope.addColumn = function() {
        $scope.lastColumnAdded = {
                "cmd":"addColumn", 
                "body":{
                  "title":"title2", 
                  "unifiedRequests":[
                    {"service":"twitter.wall","args":{}},
                    {"service":"twitter.user", "args":{"username":"RmManeschi"}},
                    {"service":"twitter.hashtag", "args":{"hashtag":"skimbo"}}
                    // {"service":"facebook.wall","args":{}}
                    //{"service":"trello.notifications","args":{}}
                    //{"service":"linkedin.wall","args":{}}
                  ]
                }
        };
        socket.send(JSON.stringify($scope.lastColumnAdded));
    };

    $scope.modifyColumn = function(column) {
      column.showModifyColumn=!(column.showModifyColumn);
      column.oldTitle = column.title;
      if (column.showModifyColumn == true) {
          var json = {"cmd":"allUnifiedRequests"};
          socket.send(JSON.stringify(json));
      }
    };

    $scope.changeColumn = function(column) {
      console.log("columnTitle",column.title);
      var json = {"cmd":"modColumn", 
                   "body":{
                     "title": column.oldTitle, 
                     "column":{
                       "title":column.title,
                       "unifiedRequests":[
                         {"service":"twitter.wall","args":{}},
                          {"service":"twitter.user", "args":{"username":"RmManeschi"}},
                          {"service":"twitter.hashtag", "args":{"hashtag":"skimbo"}}
                       ]
                     }
                   }
                  };
      socket.send(JSON.stringify(json));
    }

    $scope.deleteColumn = function(column) {
      $scope.lastColumnDeleted = {"cmd":"delColumn", "body":{"title": column.title}};
      socket.send(JSON.stringify($scope.lastColumnDeleted));
    }

    $scope.isServiceActiveForColumn = function(column, socialNetworksService) {
      for (var i = 0; i < column.unifiedRequests.length; i++) {
        if (column.unifiedRequests[i].service == socialNetworksService) {
          return true;
        }
      }
      return false;
    };

    $scope.isSocialNetworkActiveForColumn = function(column, socialNetwork) {
      for (var i = 0; i < column.unifiedRequests.length; i++) {
        var socialNetworkFromColumn = column.unifiedRequests[i].service.split(".")[0];
        if (socialNetworkFromColumn == socialNetwork) {
          return true;
        }
      };
      return false;
    }

function getColumnByName(name) {
    for (var i = 0; i < $scope.columns.length; i++) {
        if ($scope.columns[i].title == name) {
            return $scope.columns[i];
        }
    }
}

function executeCommand(socket, data) {
    if(data.cmd == "allUnifiedRequests") {
        $scope.$apply(function() {
            var socialNetworks = new Array();
            for (var i = 0; i < data.body.length; i++) {
                var elementBody = data.body[i];
                socialNetworks.unshift(elementBody);
            }
            $scope.socialNetworks = socialNetworks;
        });
    }
    if(data.cmd == "msg") {
        var columnTitle = data.body.column;
        var column = getColumnByName(columnTitle);
        console.log(data.body.column);
        $scope.$apply(function() {
            if(column.messages == undefined) {
                column.messages = [];
            }
            column.messages.push(data.body.msg);
        });
    }
    if(data.cmd == "addColumn" && data.body == "Ok") {
        $scope.$apply(function() {
            if($scope.columns == undefined) {
                $scope.columns = [];
            }
            $scope.columns.push($scope.lastColumnAdded.body);
            $scope.lastColumnAdded = undefined;
        });
    }
    if(data.cmd == "allColumns") {
        $scope.$apply(function() {
            // if($scope.columns == undefined) {
                $scope.columns = [];
            // }
            var cols = data.body;
            for (var i = 0; i < cols.length; i++) {
                var element = cols[i];
                element.showModifyColumn = false;
                $scope.columns.push(element);
            }
        });
    }
    if(data.cmd == "delColumn" && data.body == "Ok") {
      $scope.$apply(function() {
        var title = $scope.lastColumnDeleted.body.title;
        var columnIndice = 0;
        var columnFind = false;
        for (var i = 0; i < $scope.columns.length; i++) {
          if($scope.columns[i].title == title) {
            columnIndice = i;
            columnFind = true;
            break;
          }
        }
        if(columnFind) {
          $scope.columns.splice(columnIndice,1);
        }
        $scope.lastColumnDeleted = undefined;
      });
    }
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
