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

    $scope.truncateString = function(chaine) {
      if(chaine.length > 120) { 
        return String(chaine).substring(0, 120)+"...";
      }
      else {
        return chaine;
      }
    }

    $scope.addColumn = function() {
        $scope.lastColumnAdded = {
                "cmd":"addColumn", 
                "body":{
                  "title":"title2", 
                  "unifiedRequests":[
                     {"service":"twitter.wall","args":{}}
                    // {"service":"twitter.user", "args":{"username":"RmManeschi"}},
                    // {"service":"twitter.hashtag", "args":{"hashtag":"skimbo"}}
                    // {"service":"facebook.wall","args":{}}
                    //{"service":"trello.notifications","args":{}}
                    //{"service":"linkedin.wall","args":{}}
                    {"service":"viadeo.wall","args":{}}
                  ],
                  "showModifyColumn":"true",
                  "newColumn":"true"
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

    $scope.addService = function(service, column) {
      if(service.socialNetworkToken) {
        var serviceJson = {"service":service.socialNetwork+"."+service.typeService,
                            "args":service.args
                          };
        column.unifiedRequests.push(serviceJson);
      }
      else {
        openPopup(service.socialNetwork);
      }
    }

    $scope.deleteService = function(service, column) {
      var existInColumn = false;
      var indexInColumn = -1;
      for (var i = 0; i < column.unifiedRequests.length; i++) {
        if(column.unifiedRequests[i].service == service.service) {
          existInColumn = true;
          indexInColumn = i;
          break;
        }
      }
      if(existInColumn && indexInColumn > -1) {
        column.unifiedRequests.splice(indexInColumn,1);
      }
    }

    $scope.changeColumn = function(column) {
      console.log("columnTitle",column.title);
      var json = {"cmd":"modColumn", 
                   "body":{
                     "title": column.oldTitle, 
                     "column":{
                       "title":column.title,
                       "unifiedRequests": column.unifiedRequests
                     }
                   }
                  };
      socket.send(JSON.stringify(json));
    }

    $scope.deleteColumn = function(column) {
      $scope.lastColumnDeleted = {"cmd":"delColumn", "body":{"title": column.title}};
      socket.send(JSON.stringify($scope.lastColumnDeleted));
    }

    $scope.hasArguments = function(service) {
      if(Object.keys(service.args).length > 0) {
        return true;
      }
      return false;
    }

    $scope.getTypeByService = function(service) {
      return service.split(".")[1];
    }

    $scope.serviceHasTypeChar = function(service) {
      if(service.typeServiceChar != "") {
        return true;
      }
      return false;
    }

    $scope.socialNetworkByServiceName = function(service) {
      return service.split(".")[0];
    }

    $scope.typeServiceCharByService = function(service) {
      var socialNetworkName = $scope.socialNetworkByServiceName(service);
      var typeService = service.split(".")[1];
        if(typeService == "group") {
          return "ഹ";
        }
        else if(typeService == "user") {
          if(socialNetworkName == "twitter") {
            return "@";
          }
          else {
            return "😊";
          }
        }
        else if (typeService == "hashtag") {
          return "#";
        }
        return "";
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
            var serviceProposes = new Array();
            for (var i = 0; i < data.body.length; i++) {
                var elementBodySocialNetwork = data.body[i];
                var services = elementBodySocialNetwork.services;
                for (var j = 0; j < services.length; j++) {
                  var service = {socialNetwork:elementBodySocialNetwork.endpoint,
                                  socialNetworkToken:elementBodySocialNetwork.hasToken,
                                  typeService:services[j].service.split(".")[1],
                                  typeServiceChar:"",
                                  args:{}
                                };
                  service.typeServiceChar = $scope.typeServiceCharByService(services[j].service);
                  for (var k = 0; k < services[j].args.length; k++) {
                    service.args[services[j].args[k]] = "";
                  };
                  serviceProposes.push(service);
                }
            };
            $scope.serviceProposes = serviceProposes;

            var socialNetworks = new Array();
            for (var i = 0; i < data.body.length; i++) {
                var elementBody = data.body[i];
                socialNetworks.unshift(elementBody);
            }
            $scope.socialNetworks = socialNetworks;
        });
    } else if(data.cmd == "msg") {
        var columnTitle = data.body.column;
        var column = getColumnByName(columnTitle);
        $scope.$apply(function() {
            if(column.messages == undefined) {
                column.messages = [];
            }

            column.messages.push(data.body.msg);

            var insertSort = function(sortMe) {
              for(var i=0, j, tmp; i<sortMe.length; ++i ) {
                tmp = sortMe[i];
                for(j=i-1; j>=0 && sortMe[j].createdAt < tmp.createdAt; --j) {
                  sortMe[j+1] = sortMe[j];
                }
                sortMe[j+1] = tmp;
              }
            }
            insertSort(column.messages);
            console.log(data.body.msg.from,data.body.msg.createdAt);
        });
    } else if(data.cmd == "addColumn" && data.body == "Ok") {
        $scope.$apply(function() {
            if($scope.columns == undefined) {
                $scope.columns = [];
            }
            $scope.columns.push($scope.lastColumnAdded.body);
            $scope.lastColumnAdded = undefined;
        });
    } else if(data.cmd == "allColumns") {
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
    } else if(data.cmd == "delColumn" && data.body == "Ok") {
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
    } else {
      console.error("cmd not found : ", data);
    }
}

function openPopup(socialNetworkName) {
  newwindow=window.open("/auth/"+socialNetworkName,'name','max-height=600,max-width=600');
  if (window.focus) {newwindow.focus()}
  return false;
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
