'use strict';

define(["app"], function(app) {

app.controller('ColumnController', [
  "$scope", "Network", "$rootScope", "UnifiedRequestUtils", "Visibility", 
  "PopupProvider", "ArrayUtils", "ColumnSize",
  function($scope, $network, $rootScope, $unifiedRequestUtils, $visibility, 
    $popupProvider, $arrayUtils, $columnSize) {
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
      $scope.$apply(function() {
        $columnSize.setSize(columns);
        $columnSize.buildSizeCompo(columns);
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
        $arrayUtils.sortMsg(column.messages);
        $visibility.notifyNewMessage();
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
      });
    });

    $rootScope.$on('delColumn', function(evt, data) {
      var index = $arrayUtils.indexOf($scope.columns, $scope.lastColumnDeleted.body, "title");
      if(index > -1) {
        if(!$scope.$$phase) {
          $scope.$apply(function() {
            $scope.columns.splice(index, 1);
          });
        } else {
          $scope.columns.splice(index, 1);
        }
      }
      $scope.lastColumnDeleted = undefined;
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
            "oldTitle": "",
            "showModifyColumn": "true",
            "newColumn": "true",
            "unifiedRequests": [],
            "index": $scope.columns.length,
            "width": -1,
            "height": -1
          }
        );
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
      } else {
        column.title = column.oldTitle;
        column.showErrorBlankArg = false;
        for (var i = column.unifiedRequests.length - 1; i >= 0; i--) {
          if(column.unifiedRequests[i].fromServer == false) {
            column.unifiedRequests.splice(i, 1);
          }
        };
      }
    };

    $scope.resizeColumn = function(column, height, width) {
      $network.send({
        cmd: "resizeColumn", 
        body: {
          "columnTitle": column.title,
          "height": height,
          "width": width
      }});
      $columnSize.resizeColumn(column, height, width);
    }

    $scope.addService = function(service, column) {
      if(service.hasParser) {
        if(service.socialNetworkToken) {
          var clientUnifiedRequest = $unifiedRequestUtils.serverToUnifiedRequest(service.service);
          clientUnifiedRequest.fromServer = false;
          column.unifiedRequests.push(clientUnifiedRequest);
        }
        else {
          $popupProvider.openPopup(service, function() {
            $scope.$apply(function() {
              var clientUnifiedRequest = $unifiedRequestUtils.serverToUnifiedRequest(service.service);
              clientUnifiedRequest.fromServer = false;
              column.unifiedRequests.push(clientUnifiedRequest);
            });
          });
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
        json = {
          "cmd": "modColumn", 
          "body": {
            "title": column.oldTitle, 
            "column": {
              "title": column.title,
              "unifiedRequests": unifiedRequests,
              "index": column.index,
              "width": column.width,
              "height": column.height
            }
          }
        };
      } else {
        column.newColumn = false;
        json = {
          "cmd": "addColumn", 
          "body": {
            "title": column.title,
            "unifiedRequests": unifiedRequests,
            "index": column.index,
            "width": column.width,
            "height": column.height
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
      if(!column.newColumn) {
        $network.send($scope.lastColumnDeleted);
      } else {
        $rootScope.$broadcast('delColumn', {});
      }
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

}]);

return app;
});