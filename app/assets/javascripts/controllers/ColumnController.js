'use strict';

define(["app"], function(app) {

app.controller('ColumnController', [
  "$scope", "Network", "$rootScope", "Visibility", "DataCache",
  "ArrayUtils", "ColumnSize", "$window", "$timeout",
  function($scope, $network, $rootScope, $visibility, $dataCache,
    $arrayUtils, $columnSize, $window, $timeout) {

    var tempColumns;

    $scope.globalContainerSize = "100%";
    $scope.columns = [];

    angular.element($window).bind('resize', function () {
      $columnSize.setSize($scope.columns);
      if($columnSize.isMobileSize() && $scope.columns.length > 0) {
        $scope.globalContainerSize = $scope.columns.length+"10%";
      } else {
        $scope.globalContainerSize = "100%";
      }
      $scope.$apply();
    });

    $dataCache.on('allColumns', function(columns) {
      if($scope.columns.length === 0) {//on deco/reco will not clear all columns
        var copy = columns.slice(0);

        $columnSize.setSize(copy);
        $columnSize.buildSizeCompo(copy);
        $scope.columns = copy;
        if($scope.columns.length === 0) {
          $rootScope.$broadcast('glassShowView', 'help');
        }
        if($columnSize.isMobileSize() && $scope.columns.length > 0) {
          $scope.globalContainerSize = $scope.columns.length+"10%";
        } else {
          $scope.globalContainerSize = "100%";
        }
        for (var i = 0; i < $scope.columns.length; i++) {
          var originalColumn = $scope.columns[i];
          if(tempColumns != undefined && tempColumns[originalColumn.title]) {
            var temp = tempColumns[originalColumn.title];
            for (var h = 0; h < temp.messages.length; h++) {
              addMsg(temp.messages[h], originalColumn.title);
            };
          }
        }

        $scope.$apply();
      }
    });

    $dataCache.on('addColumn', function(column) {
      $columnSize.setSize([column]);
      $columnSize.buildSizeCompo([column]);
      $scope.columns.push(column);
    });

    $dataCache.on('modColumn', function(newColumn) {
      var index = newColumn[0];
      var column = newColumn[1];
      var oldColumn = $scope.columns[index];
      $columnSize.setSize([column]);
      $columnSize.buildSizeCompo([column]);
      $scope.columns[index] = column;
      for (var i = 0; i < oldColumn.messages.length; i++) {
        addMsg(oldColumn.messages[i], column.title);
      };
    });

    $dataCache.on('delColumn', function(column) {
      $scope.columns.splice(column[0], 1);
    });

    $rootScope.$on('msg', function(evt, msg) {
      $scope.$apply(function() {
        msg.msg.isView = false;
        addMsg(msg.msg, msg.column);
      });
    });

    $rootScope.$on('moveColumnEvent', function(evt, dragged, dropped) {
      var i, oldIndex1, oldIndex2;
      for(i=0; i<$scope.columns.length; i++) {
        var c = $scope.columns[i];
        if(dragged.title === c.title) {
          oldIndex1 = i;
        }
        if(dropped.title === c.title) {
          oldIndex2 = i;
        }
      }
      var temp = $scope.columns[oldIndex1];
      $scope.columns[oldIndex1] = $scope.columns[oldIndex2];
      $scope.columns[oldIndex2] = temp;
      $scope.$apply();
    });

    $rootScope.$on('dropColumnEvent', function(evt) {
      var modColumnsOrder = {
        "cmd":"modColumnsOrder", "body":{ 
          "columns": []
        }
      };
      for (var i = 0; i < $scope.columns.length; i++) {
        modColumnsOrder.body.columns.push($scope.columns[i].title);
      };
      $network.send(modColumnsOrder);
    });

    $scope.modifyColumn = function(column) {
      $rootScope.$broadcast('glassShowView', 'modifColumn', function() {
        $rootScope.$broadcast('clientModifyColumn', column);
      });
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
          if(column.messages[i].isView == false) {
            $visibility.notifyMessageRead();
          }
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
      $rootScope.$broadcast('glassShowView', '/assets/app/views/post.html', function() {
        $rootScope.$broadcast('dispatchMsg', message);
      });
    }

    $scope.comment = function(message) {
      message.inComment = !message.inComment;
      if(message.from === "twitter") {
        message.currentComment = "@" + message.authorScreenName;
      }
    }

    $scope.sendComment = function(message, column) {
      message.styleRefresh = "rotate";
      $network.send({
        cmd: "comment",
        body: {
          "serviceName": message.service,
          "providerId": message.idProvider,
          "message": message.currentComment,
          "columnTitle": column.title
        }
      });
      message.inComment = false;
      $scope.$apply();
    }

    $scope.star = function(message, column) {
      message.styleRefresh = "rotate";
      $network.send({
        cmd: "star",
        body: {
          "serviceName": message.service,
          "id": message.idProvider,
          "columnTitle": column.title
        }
      });
    }

    $scope.blurText = false;

    $scope.switchBlur = function() {
      $scope.$apply(function() {
        $scope.blurText = !$scope.blurText;
      });
    };

    function addMsg(msg, columnName) {
      var column = getColumnByName(columnName);
      column.messages = column.messages || [];
      if(column.isTemp !== true) {
        $arrayUtils.sortMsg(column.messages, msg);
      } else {
        column.messages.push(msg);
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
      
      tempColumns = tempColumns || [];
      tempColumns[name] = tempColumns[name] || {isTemp: true};
      return tempColumns[name];
    }

    $scope.showMedias = undefined;

    $scope.showMediasClick = function(message) {
      $scope.showMedias = "/assets/app/views/medias.html";
      $timeout(function() {
        $rootScope.$broadcast('displayMedias', message);
      }, 10);
    }

}]);

return app;
});