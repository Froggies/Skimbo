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

    $scope.columns = [];
    $scope.userNoColumn = false;

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
        if($scope.columns.length == 0) {
          $scope.columns = columns;
        }
        $scope.userNoColumn = $scope.columns.length === 0;
      });
    });

    $rootScope.$on('msg', function(evt, msg) {
      $scope.$apply(function() {
        var column = getColumnByName(msg.column);
        if(column.messages == undefined) {
          column.messages = [];
          console.log(msg);
        }
        $arrayUtils.sortMsg(column.messages, msg.msg);
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

    $rootScope.$on('delColumn', function(evt, column) {
      var index = $arrayUtils.indexOf($scope.columns, column, "title");
      if(index > -1) {
        if(!$scope.$$phase) {
          $scope.$apply(function() {
            $scope.columns.splice(index, 1);
          });
        } else {
          $scope.columns.splice(index, 1);
        }
      }
    });

    $rootScope.$on('addColumn', function(evt, column) {
      $scope.$apply(function() {
        $columnSize.setSize([column]);
        $columnSize.buildSizeCompo([column]);
        $scope.columns.push(column);
        $scope.userNoColumn = $scope.columns.length === 0;
      });
    });

    $rootScope.$on('modColumn', function(evt, column) {
      $scope.$apply(function() {
        console.log(column);
        var c = getColumnByName(column.column.title);
        c.unifiedRequests = column.column.unifiedRequests;
        c.messages = [];
      });
    });

    $scope.modifyColumn = function(column) {
      $rootScope.$broadcast('clientModifyColumn', column);
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

    $scope.messagesNoView = function(column) {
      var nb = 0;
      if(column != undefined && column.messages != undefined) {
        for(var i=0; i<column.messages.length; i++) {
          if(column.messages[i].isView == false) {
            nb++;
          }
        }
      }
      return nb;
    }

    $scope.markAllAsView = function(column) {
      $rootScope.$broadcast("scrollManagerGoTop", column);
      if(column != undefined && column.messages != undefined) {
        for(var i=0; i<column.messages.length; i++) {
          column.messages[i].isView = true;
        }
      }
    }

    $scope.getDetails = function(message, column) {
      message.styleRefresh = "rotate";
      $network.send({
        cmd: "detailsSkimbo", 
        body: {
          "serviceName": message.service,
          "id": message.idProvider,
          "columnTitle": column.title
      }});
    }

    $scope.dispatchMsg = function(message) {
      $rootScope.$broadcast("dispatchMsg", message);
    }

    $scope.blurText = false;

    $scope.switchBlur = function() {
      $scope.$apply(function() {
        $scope.blurText = !$scope.blurText;
      });
    };

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