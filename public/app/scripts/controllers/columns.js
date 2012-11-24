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
            executeCommand(data);
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
            executeCommand(data);
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
        if($scope.serviceProposes == undefined) {
          var json = {"cmd":"allUnifiedRequests"};
          socket.send(JSON.stringify(json));
        }
        if($scope.columns == undefined) {
          $scope.columns = [];
        }
        $scope.columns.push({"title":"",
                              "oldTitle":"",
                              "showModifyColumn":"true",
                              "newColumn":"true",
                              "unifiedRequests":[]});
    };

    $scope.modifyColumn = function(column) {
      column.showModifyColumn = !column.showModifyColumn;
      if (column.showModifyColumn == true) {
        if($scope.serviceProposes == undefined) {
          var json = {"cmd":"allUnifiedRequests"};
          socket.send(JSON.stringify(json));
        }
      }
      if(column.showModifyColumn == true) {
        column.oldTitle = column.title;
      }
      else {
        column.title = column.oldTitle;
      }
    };

    $scope.addService = function(service, column) {
      if(service.socialNetworkToken) {
        var clientUnifiedRequest = serverToUnifiedRequest(service.service);
        column.unifiedRequests.push(clientUnifiedRequest);
      }
      else {
        $scope.openPopup(service, column);
      }
    }

    $scope.deleteService = function(service, column) {
      var indexInColumn = -1;
      for (var i = 0; i < column.unifiedRequests.length; i++) {
        if(column.unifiedRequests[i].service == service.service) {
          indexInColumn = i;
          break;
        }
      }
      if(indexInColumn > -1) {
        column.unifiedRequests.splice(indexInColumn,1);
      }
    }

    $scope.changeColumn = function(column) {
      column.showErrorTitleAlreadyExist = false;
      column.showErrorTitleRequired = false;
      var json = "";
      var unifiedRequests = [];
      for (var i = 0; i < column.unifiedRequests.length; i++) {
        unifiedRequests.push(clientToServerUnifiedRequest(column.unifiedRequests[i]));
      };
      if(!column.newColumn) {
        json = {"cmd":"modColumn", 
                 "body":{
                   "title": column.oldTitle, 
                   "column":{
                     "title":column.title,
                     "unifiedRequests": unifiedRequests
                   }
                 }
                };
      } else {
        column.newColumn = false;
        json = {"cmd":"addColumn", 
                "body":{
                  "title":column.title,
                  "unifiedRequests": unifiedRequests
                }
              };
      }
      if(column.title =="") {
        column.showErrorTitleRequired = true;
      }
      else {
        column.showErrorTitleRequired = false;
        var nombreName = 0;
        for (var i = 0; i < $scope.columns.length; i++) {
          if($scope.columns[i].title == column.title) {
            nombreName++;
          }
        };
        if(nombreName > 1) {
          column.showErrorTitleAlreadyExist = true;
        }
        else {
          column.showErrorTitleAlreadyExist = false;
          column.messages = [];
          column.showModifyColumn= !column.showModifyColumn;
          socket.send(JSON.stringify(json));
        }
      }
    }

    $scope.deleteColumn = function(column) {
      $scope.lastColumnDeleted = {"cmd":"delColumn", "body":{"title": column.title}};
      socket.send(JSON.stringify($scope.lastColumnDeleted));
    }

    $scope.serviceHasTypeChar = function(service) {
      if(service.typeServiceChar != "") {
        return true;
      }
      return false;
    }

    $scope.typeServiceCharByService = function(service) {
      var socialNetworkName = service.split(".")[0];
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

    $scope.openPopup = function(service, column) {
      var _service = service;
      var _column = column;
      var scope = $scope;
      var newwindow = window.open("/auth/"+service.socialNetwork, 'Connexion', 'height=500,width=500');

      if (newwindow !== undefined) {
        if (window.focus) newwindow.focus();
        
        newwindow.onclose = function() {
          $http.get("/api/providers/services").success(function(data) {
            executeCommand({"cmd":"allUnifiedRequests","body":data});
            var clientUnifiedRequest = serverToUnifiedRequest(_service.service);
            _column.unifiedRequests.push(clientUnifiedRequest);

          });
        };
        newwindow.onbeforeunload = function() {
          $http.get("/api/providers/services").success(function(data) {
            executeCommand({"cmd":"allUnifiedRequests","body":data});
            var clientUnifiedRequest = serverToUnifiedRequest(_service.service);
            _column.unifiedRequests.push(clientUnifiedRequest);
          });
        };
      }
      return false;
    }

    $scope.deleteProvider = function(providerName) {
      var cmd = {"cmd":"deleteProvider", "body":{"provider": providerName}};
      socket.send(JSON.stringify(cmd));
      for (var i = 0; i < $scope.userInfos.length; i++) {
        if($scope.userInfos[i].socialType == providerName) {
          $scope.userInfos.splice(i,1);
          break;
        }
      }
    }

function getColumnByName(name) {
    for (var i = 0; i < $scope.columns.length; i++) {
        if ($scope.columns[i].title == name) {
            return $scope.columns[i];
        }
    }
}


function fillExplainService(typeService, socialNetwork) {
  if(typeService == "group") {
    return "Click here to display a specific Facebook group.";
  }
  else if(typeService == "user") {
    if(socialNetwork == "twitter") {
      return "Click here to display tweets of a specific Twitter user.";
    }
    else {
      return "Click here to display the wall of a specific Facebook user.";
    }
  }
  else if (typeService == "hashtag") {
    return "Click here to display tweets of a specific Twitter hashtag.";
  }
  else {
    return "Click here to display your "+socialNetwork+" "+typeService+".";
  }
}

function checkExistingImage(image) {
  if(image == "" || image == undefined) {
    return "assets/img/image-default.png";
  }
  else {
    return image;
  }
}

function executeCommand(data) {
    if(data.cmd == "allUnifiedRequests") {
      var serviceProposes = new Array();
      for (var i = 0; i < data.body.length; i++) {
          var elementBodySocialNetwork = data.body[i];
          var services = elementBodySocialNetwork.services;
          for (var j = 0; j < services.length; j++) {
            var service = {socialNetwork:elementBodySocialNetwork.endpoint,
                            socialNetworkToken:elementBodySocialNetwork.hasToken,
                            typeService:services[j].service.split(".")[1],
                            typeServiceChar:"",
                            explainService:"",
                            args:{},
                            service:services[j]
                          };
            service.explainService = fillExplainService(service.typeService, service.socialNetwork);
            service.typeServiceChar = $scope.typeServiceCharByService(services[j].service);
            for (var k = 0; k < services[j].args.length; k++) {
              service.args[services[j].args[k]] = "";
            };
            serviceProposes.push(service);
          }
      };
      if(!$scope.$$phase) {
        $scope.$apply(function() {
          $scope.serviceProposes = serviceProposes;
        });
      }
      else {
        $scope.serviceProposes = serviceProposes;
      }
    } else if(data.cmd == "msg") {
        var columnTitle = data.body.column;
        var column = getColumnByName(columnTitle);
        $scope.$apply(function() {
            if(column.messages == undefined) {
                column.messages = [];
            }
            data.body.msg.authorAvatar = checkExistingImage(data.body.msg.authorAvatar);
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
        });
    } else if(data.cmd == "allColumns") {
        $scope.$apply(function() {
            $scope.columns = [];
            var cols = data.body;
            for (var i = 0; i < cols.length; i++) {
                var originalColumn = cols[i];
                var clientColumn = {};
                clientColumn.title = originalColumn.title;
                clientColumn.unifiedRequests = [];
                for (var j = 0; j < originalColumn.unifiedRequests.length; j++) {
                  var unifiedRequest = originalColumn.unifiedRequests[j];
                  var clientUnifiedRequest = serverToClientUnifiedRequest(unifiedRequest);
                  clientColumn.unifiedRequests.push(clientUnifiedRequest);
                };
                clientColumn.showModifyColumn = false;
                $scope.columns.push(clientColumn);
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
    } else if(data.cmd == "userInfos") {
      $scope.$apply(function() {
        if($scope.userInfos == undefined) {
          $scope.userInfos = [];
        }
        data.body.avatar = checkExistingImage(data.body.avatar);
        $scope.userInfos.push(data.body);
      });
    }
    else {
      console.error("cmd not found : ", data);
    }
}

  function serverToUnifiedRequest(unifiedRequest) {
    var clientUnifiedRequest = {};
    clientUnifiedRequest.service = unifiedRequest.service;
    clientUnifiedRequest.providerName = unifiedRequest.service.split(".")[0];
    clientUnifiedRequest.serviceName = unifiedRequest.service.split(".")[1];
    clientUnifiedRequest.args = []
    for (var index in unifiedRequest.args) {
      var key = unifiedRequest.args[index];
      clientUnifiedRequest.args.push({"key":key,"value":""});
    };
    clientUnifiedRequest.hasArguments = clientUnifiedRequest.args.length > 0;
    return clientUnifiedRequest;
  }

  function serverToClientUnifiedRequest(unifiedRequest) {
    var clientUnifiedRequest = {};
    clientUnifiedRequest.service = unifiedRequest.service;
    clientUnifiedRequest.providerName = unifiedRequest.service.split(".")[0];
    clientUnifiedRequest.serviceName = unifiedRequest.service.split(".")[1];
    clientUnifiedRequest.args = []
    for (var key in unifiedRequest.args) {
      var value = unifiedRequest.args[key];
      console.log("value", value);
      clientUnifiedRequest.args.push({"key":key,"value":value});
    };
    clientUnifiedRequest.hasArguments = clientUnifiedRequest.args.length > 0;
    return clientUnifiedRequest;
  }

  function clientToServerUnifiedRequest(unifiedRequest) {
    var serverUnifiedRequest = {};
    serverUnifiedRequest.service = unifiedRequest.providerName+"."+unifiedRequest.serviceName;
    serverUnifiedRequest.args = {}
    for (var index in unifiedRequest.args) {
      var arg = unifiedRequest.args[index];
      serverUnifiedRequest.args[arg.key] = arg.value;
    };
    return serverUnifiedRequest;
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
