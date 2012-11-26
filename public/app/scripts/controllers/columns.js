'use strict';

publicApp.controller('ColumnsCtrl', function($scope, $http) {

    //chrome memory leak !!!
    $scope.$destroy= function() {
      if ($rootScope == this) return; // we can't remove the root node;
        var parent = this.$parent;

        this.$broadcast('$destroy');

        if (parent.$$childHead == this) parent.$$childHead = this.$$nextSibling;
        if (parent.$$childTail == this) parent.$$childTail = this.$$prevSibling;
        if (this.$$prevSibling) this.$$prevSibling.$$nextSibling = this.$$nextSibling;
        if (this.$$nextSibling) this.$$nextSibling.$$prevSibling = this.$$prevSibling;

      //------- my additions -----------------------
      this.$id = null;
      this.$$phase = this.$parent = this.$$watchers =
                     this.$$nextSibling = this.$$prevSibling =
                     this.$$childHead = this.$$childTail = null;
      this['this'] = this.$root =  null;
      this.$$asyncQueue = null; // fixme: how this must be properly cleaned?
      this.$$listeners = null; // fixme: how this must be properly cleaned?

    }

    var wshost = jsRoutes.controllers.stream.WebSocket.connect().webSocketURL();
    var ssehost = jsRoutes.controllers.stream.Sse.connect().absoluteURL();
    var sseping = jsRoutes.controllers.stream.Sse.ping().absoluteURL();
    
    var socket;
    if(window.MozWebSocket) {
        window.WebSocket=window.MozWebSocket;
    }
    if(!window.WebSocket) {
        alert('Your browser doesn\'t support webSocket ! You switch in Sse mode but isn\'t recommended !');
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

        source.addEventListener('open', function(e) {console.log('sse socket ouverte');}, false);
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
          $http.get("/api/providers/services").success(function(data) {
            executeCommand({"cmd":"allUnifiedRequests","body":data});
          });
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
          $http.get("/api/providers/services").success(function(data) {
            executeCommand({"cmd":"allUnifiedRequests","body":data});
          });
        }
      }
      if(column.showModifyColumn == true) {
        column.oldTitle = column.title;
      }
      else {
        column.title = column.oldTitle;
        column.showErrorBlankArg = false;
        for (var i = column.unifiedRequests.length - 1; i >= 0; i--) {
          if(column.unifiedRequests[i].fromServer == false) {
            column.unifiedRequests.splice(i,1);
          }
        };
      }
    };

    $scope.addService = function(service, column) {
      if(service.socialNetworkToken) {
        var clientUnifiedRequest = serverToUnifiedRequest(service.service);
        clientUnifiedRequest.fromServer = false;
        column.unifiedRequests.push(clientUnifiedRequest);
      }
      else {
        $scope.openPopup(service, column);
      }
    }

    $scope.deleteService = function(service, column, arg) {
      var indexInColumn = -1;
      var found = false;
      for (var i = 0; !found && i < column.unifiedRequests.length; i++) {
        if(column.unifiedRequests[i].service == service.service && 
            column.unifiedRequests[i].args.length > 0) {
          for (var j = 0; !found && j < column.unifiedRequests[i].args.length; j++) {
            var a = column.unifiedRequests[i].args[j];
            if(arg.key == a.key && arg.value == a.value) {
              found = true;
              column.unifiedRequests.splice(i,1);
            }
          }
        }
        else if(column.unifiedRequests[i].service == service.service && arg === undefined) {
          found = true;
          column.unifiedRequests.splice(i,1);
        }
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

      column.showErrorBlankArg = false;

      for (var i = 0; i < column.unifiedRequests.length; i++) {
        for (var j = 0; j < column.unifiedRequests[i].args.length; j++) {
          if (column.unifiedRequests[i].args[j].value == "") {
            column.showErrorBlankArg = true;
            break;
          }
        };
      };
     
      if (!column.showErrorBlankArg) {
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

    $scope.reconnect = function(socialNetworkName) {
      window.sendMessageToWindow = function() {
        $scope.$apply(function() {
          for (var i = $scope.notifications.length - 1; i >= 0; i--) {
            if ($scope.notifications[i].providerName == socialNetworkName) {
              $scope.notifications.splice(i,1);
            }
          };
        });
      };
      $scope.openPopup({"socialNetwork": socialNetworkName});
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
            console.log("dans close",$scope.notifications);
            executeCommand({"cmd":"allUnifiedRequests","body":data});
            if(_column != undefined) {
              var clientUnifiedRequest = serverToUnifiedRequest(_service.service);
              _column.unifiedRequests.push(clientUnifiedRequest);
            }
          });
        };
        newwindow.onbeforeunload = function() {
          $http.get("/api/providers/services").success(function(data) {
            console.log("dans onbeforeunload",$scope.notifications);
            executeCommand({"cmd":"allUnifiedRequests","body":data});
            if(_column != undefined) {
              var clientUnifiedRequest = serverToUnifiedRequest(_service.service);
              _column.unifiedRequests.push(clientUnifiedRequest);
            }
          });
        };
      }
      return false;
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

            data.body.msg.dateAgo = moment(moment(Number(data.body.msg.createdAt)), "YYYYMMDD").fromNow();

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
                  clientUnifiedRequest.fromServer = true;
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
      $scope.$parent.$parent.$apply(function() {
        if($scope.$parent.$parent.userInfos == undefined) {
          $scope.$parent.$parent.userInfos = [];
        }
        data.body.avatar = checkExistingImage(data.body.avatar);
        $scope.$parent.$parent.userInfos.push(data.body);
      });
    }
    else if (data.cmd == "tokenInvalid") {
      console.log("tokenInvalid");
      $scope.$apply(function() {
        if($scope.notifications == undefined) {
          $scope.notifications = [];
        }
        var notificationExiste = false;
        for (var i = 0; i < $scope.notifications.length; i++) {
          if ($scope.notifications[i].providerName == data.body.providerName) {
            notificationExiste = true;
            break;
          }
        };
        if(!notificationExiste) {
          $scope.notifications.push(data.body);
        }
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
