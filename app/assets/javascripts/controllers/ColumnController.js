'use strict';

controllers.controller('ColumnController', [
  "$scope", "Network", "$rootScope", "UnifiedRequestUtils",
  function($scope, $network, $rootScope, $unifiedRequestUtils) {

    //chrome memory leak !!!
    $scope.$destroy= function() {
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

    var isPageVisible = true;
    try {
      isPageVisible = document.hasFocus();
    } catch(error) {
      isPageVisible = true;
    }
    
    var nbNewMessages = 0;
    pageVisibility(switchPageVisible, switchPageInvisible);

    $scope.send = $network.send;

    $rootScope.$on('availableServices', function(evt, serviceProposes) {
      if(!$scope.$$phase) {
          $scope.$apply(function() {
            $scope.serviceProposes = serviceProposes;
          });
        }
        else {
          $scope.serviceProposes = serviceProposes;
        }
    });

    $rootScope.$on('allColumns', function(evt, columns) {
      console.log(columns);
      $scope.$apply(function() {
        $scope.columns = columns;
      });
    });

    $rootScope.$on('msg', function(evt, msg) {
      $scope.$apply(function() {
        var column = getColumnByName(msg.column);
        
        if(column.messages == undefined) {
          column.messages = [];
        }
        column.messages.push(msg.msg);
        var insertSort = function(sortMe) {
          for(var i=0, j, tmp; i<sortMe.length; ++i ) {
            tmp = sortMe[i];
            tmp.dateAgo = moment(moment(Number(tmp.createdAt)), "YYYYMMDD").fromNow();
            for(j=i-1; j>=0 && sortMe[j].createdAt < tmp.createdAt; --j) {
              sortMe[j+1] = sortMe[j];
            }
            sortMe[j+1] = tmp;
          }
        }
        insertSort(column.messages);
        notifyNewMessage();
      });
    });

    $rootScope.$on('tokenInvalid', function(evt, data) {
      $scope.$apply(function() {
        if($scope.notifications == undefined) {
          $scope.notifications = [];
        }
        var notificationExiste = false;
        for (var i = 0; i < $scope.notifications.length; i++) {
          if ($scope.notifications[i].providerName == data.providerName) {
            notificationExiste = true;
            break;
          }
        };
        if(!notificationExiste) {
          $scope.notifications.push(data);
        }
      });
    });

    $rootScope.$on('newToken', function(evt, data) {
      $scope.$apply(function() {
        if($scope.serviceProposes != undefined) {
          for (var i = 0; i < $scope.serviceProposes.length; i++) {
            if($scope.serviceProposes[i].service.service.split(".")[0] == data.providerName) {
              $scope.serviceProposes[i].socialNetworkToken = true;
            }
          };
        }

        if($scope.notifications != undefined) {
          for (var i = $scope.notifications.length - 1; i >= 0; i--) {
            if ($scope.notifications[i].providerName == data.providerName) {
              $scope.notifications.splice(i,1);
            }
          };
        }
      });
    });

    $rootScope.$on('error', function(evt, data) {
      $scope.$apply(function() {
        if($scope.notifications == undefined) {
          $scope.notifications = [];
        }
        var notificationExiste = false;
        for (var i = 0; i < $scope.notifications.length; i++) {
          if ($scope.notifications[i].providerName == data.providerName && 
            $scope.notifications[i].title == data.msg) {
            notificationExiste = true;
            break;
          }
        };
        if(!notificationExiste) {
          $scope.notifications.push(data);
        }
      });
    });

    $rootScope.$on('deleteColumn', function(evt, data) {
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
    });

    $scope.addColumn = function() {
        if($scope.serviceProposes == undefined) {
          $network.send({cmd:"allUnifiedRequests"});
        }
        if($scope.columns == undefined) {
          $scope.columns = [];
        }
        $scope.columns.push(
          {
            "title": (String.fromCharCode(945+$scope.columns.length)) + ") What is here ? ",
            "oldTitle":"",
            "showModifyColumn":"true",
            "newColumn":"true",
            "unifiedRequests":[],
            "index":$scope.columns.length
          });
    };

    $scope.modifyColumn = function(column) {
      column.showModifyColumn = !column.showModifyColumn;
      if (column.showModifyColumn == true) {
        if($scope.serviceProposes == undefined) {
          $network.send({cmd:"allUnifiedRequests"});
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
      if(service.hasParser) {
        if(service.socialNetworkToken) {
          var clientUnifiedRequest = $unifiedRequestUtils.serverToUnifiedRequest(service.service);
          clientUnifiedRequest.fromServer = false;
          column.unifiedRequests.push(clientUnifiedRequest);
        }
        else {
          $scope.openPopup(service, column);
        }
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
        unifiedRequests.push($unifiedRequestUtils.clientToServerUnifiedRequest(column.unifiedRequests[i]));
      };
      if(!column.newColumn) {
        json = {"cmd":"modColumn", 
                 "body":{
                   "title": column.oldTitle, 
                   "column":{
                     "title":column.title,
                     "unifiedRequests": unifiedRequests,
                     "index":column.index,
                     "width":-1,
                     "height":-1
                   }
                 }
                };
      } else {
        column.newColumn = false;
        json = {"cmd":"addColumn", 
                "body":{
                  "title":column.title,
                  "unifiedRequests": unifiedRequests,
                  "index":column.index,
                  "width":-1,
                  "height":-1
                }
              };
      }

      column.showErrorBlankArg = false;
      var cleanRegex = /[&\/\\#,+()$~%'":*?<>{}]/g;

      for (var i = 0; i < column.unifiedRequests.length; i++) {
        for (var j = 0; j < column.unifiedRequests[i].args.length; j++) {
          column.unifiedRequests[i].args[j].value = column.unifiedRequests[i].args[j].value.replace(cleanRegex, '');
          if (column.unifiedRequests[i].args[j].value == "") {
            column.showErrorBlankArg = true;
            break;
          }
        };
      };

      column.showDoubleParser = false;
      if(!column.showErrorBlankArg) {
        var nbServiceFound = 0;
        var nbArgFound = 0;
        for (var i = 0; i < column.unifiedRequests.length && !column.showDoubleParser; i++) {
          nbServiceFound = 0;
          nbArgFound = 0;
          for (var u = 0; u < column.unifiedRequests.length && !column.showDoubleParser; u++) {
            if(column.unifiedRequests[i].args.length > 0) {
              for (var j = 0; j < column.unifiedRequests[i].args.length && !column.showDoubleParser; j++) {
                for (var h = 0; h < column.unifiedRequests[u].args.length && !column.showDoubleParser; h++) {
                  if (column.unifiedRequests[i].service == column.unifiedRequests[u].service && 
                    column.unifiedRequests[i].args[j].value == column.unifiedRequests[u].args[h].value) {
                    nbArgFound++;
                  }
                };
                if(nbArgFound > 1) {
                  column.showDoubleParser = true;
                }
              };
            } else if(column.unifiedRequests[i].service == column.unifiedRequests[u].service) {
              nbServiceFound++;
            }
          };
          if(nbServiceFound > 1) {
            column.showDoubleParser = true;
          }
        };
      }
     
      if (!column.showErrorBlankArg && !column.showDoubleParser) {
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
            $network.send(json);
          }
        }
      }
    }

    $scope.deleteColumn = function(column) {
      $scope.lastColumnDeleted = {"cmd":"delColumn", "body":{"title": column.title}};
      $network.send($scope.lastColumnDeleted);
    }

    $scope.clickOnNotification = function(notification) {
      if(notification.isError == false) {
        $scope.reconnect(notification.providerName);
      } else {
        for (var i = 0; i < $scope.notifications.length; i++) {
          var notif = $scope.notifications[i];
          if(notification.providerName == notif.providerName && notification.msg == notif.msg) {
            $scope.notifications.splice(i, 1);
            return;
          }
        };
      }
    }

    $scope.reconnect = function(socialNetworkName) {
      $scope.openPopup({"socialNetwork": socialNetworkName});
    }

    $scope.openPopup = function(service, column) {
      var _service = service;
      var _column = column;
      var scope = $scope;
      var width = 500;
      var height = 500;

      switch(service.socialNetwork ) {
        case "github":
          width = 960;
          height = 430;
        break;
        case "facebook":
         width = 640;
          height = 372;   
        break;
        case "viadeo":
          width = 570;
          height = 315;
        break;
        case "trello":
          width = 572;
          height = 610;
        break;
        case "twitter":
          width = 600;
          height = 500;
        break;
      }

      if (window.innerWidth) {
        var left = (window.innerWidth-width)/2;
        var top = (window.innerHeight-height)/2;
      } else {
         var left = (document.body.clientWidth-width)/2;
         var top = (document.body.clientHeight-height)/2;
      }
      
      var newwindow = window.open("/auth/"+service.socialNetwork, 'Connection', 'height='+height+', width='+width+', left='+left+', top='+top);
      window.callMeToRefresh = function() {
        var col = _column;
        var serv = _service;
        $network.send({cmd:"allUnifiedRequests"});
      }

      if (newwindow !== undefined && window.focus) {
        newwindow.focus();
      }
      return false;
    }


function getColumnByName(name) {
  if($scope.columns !== undefined) {
    for (var i = 0; i < $scope.columns.length; i++) {
      if ($scope.columns[i].title == name) {
          return $scope.columns[i];
      }
    }
  }
  if($rootScope.tempColumns == undefined) {
    $rootScope.tempColumns = [];
  }
  if($rootScope.tempColumns[name] == undefined) {
    $rootScope.tempColumns[name] = {};
  }
  return $rootScope.tempColumns[name];
}

function notifyNewMessage() {
  if (!isPageVisible) {
    nbNewMessages += 1;
    document.title = "("+nbNewMessages+") Skimbo";
  }
}

function switchPageVisible() {
  document.title = "Skimbo";
  nbNewMessages = 0;
  isPageVisible = true;
}

function switchPageInvisible() {
  isPageVisible = false;
  nbNewMessages = 0;
}

}]);

function dragOver(target, ev)
{
  ev.preventDefault();
}

function dragStart(target,ev)
{
	target.style.opacity = "0.7";
  ev.dataTransfer.setData('text/plain', "none");
  ev.dataTransfer.setData("text/html", target.innerHTML);
	ev.dataTransfer.setData("id", target.id);
	ev.dataTransfer.effectAllowed = 'move';
}

function dragEnter(target, ev) {
	ev.preventDefault();
}

function dragLeave(target, ev) {
	ev.preventDefault();
}

function dragEnd(target,ev)
{
	target.style.opacity = "1";
}

function drop(target,ev)
{
	ev.preventDefault();
  target.style.opacity = "1";

	var fromId = ev.dataTransfer.getData("id");
  fromId = fromId.split("_")[1];
	var toId = target.id;
  toId = toId.split("_")[1];

	var scope = angular.element(target).scope().$parent;
  scope.$apply(function() {
    var temp = scope.columns[fromId];
    scope.columns[fromId] = scope.columns[toId];
    scope.columns[fromId].index = toId;
    scope.columns[toId] = temp;
    scope.columns[toId].index = fromId;
    scope.$eval(scope.columns);
    var modColumnsOrder = {"cmd":"modColumnsOrder", "body":{}};
    modColumnsOrder.body.columns = [];
    for (var i = 0; i < scope.columns.length; i++) {
      modColumnsOrder.body.columns.push(scope.columns[i].title);
    };
    scope.send(modColumnsOrder);
  }); 

}
