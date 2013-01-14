'use strict';

define(["app"], function(app) {

app.controller('ModifColumnController', [
  "$scope", "Network", "$rootScope", "UnifiedRequestUtils", "Visibility", "PopupProvider", "ArrayUtils",
  function($scope, $network, $rootScope, $unifiedRequestUtils, $visibility, $popupProvider, $arrayUtils) {

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

    $scope.showModifyColumn = false;
    $scope.availableSocialNetworksWidth = "90%";
    $scope.column = undefined;
    $scope.columnsTitle = [];

    $rootScope.$on('allColumns', function(evt, columns) {
      //to check unique title
      $scope.$apply(function() {
        for (var i = 0; i < columns.length; i++) {
          $scope.columnsTitle.push(columns[i].title);
        };
      });
    });

    $rootScope.$on('allUnifiedRequests', function(evt, providers) {
      $scope.$apply(function() {
        $scope.providersAndServices = providers;
      });
    });

    $rootScope.$on('allProviders', function(evt, providers) {
      $scope.$apply(function() {
        $scope.providersAvailable = providers;
      });
    });

    $rootScope.$on('clientModifyColumn', function(evt, column) {
      $scope.newColumn(column);//reset view & download providers (if needed)
    });

    $rootScope.$on('addColumn', function(evt, column) {
      $scope.columnsTitle.push(column.title);
    });

    $rootScope.$on('modColumn', function(evt, column) {
      $scope.$apply(function() {
        //refresh name
        for (var i = 0; i < $scope.columnsTitle.length; i++) {
          if($scope.columnsTitle[i] == column.title) {
            $scope.columnsTitle.splice(i, 1);
            $scope.columnsTitle.push(column.column.title);
            break;
          }
        };
      });
    });

    $scope.selectSocialNetwork = function(socialNetwork) {
      $scope.availableSocialNetworksWidth = "";
      var indexOfLastSelected = $arrayUtils.indexOfWith($scope.providersAvailable, undefined, function(inArray, data) {
          return inArray.selected == true;
      });
      if($scope.providersAvailable[indexOfLastSelected] != undefined) {
        if($scope.providersAvailable[indexOfLastSelected] != socialNetwork) {
          $scope.providersAvailable[indexOfLastSelected].selected = false;
        }
      }
      var indexOfSocialNetwork = $arrayUtils.indexOf($scope.providersAvailable, socialNetwork, "name");
      $scope.providersAvailable[indexOfSocialNetwork].selected = true;
      var indexOfSocialNetworkInProvidersAndServices = $arrayUtils.indexOfWith($scope.providersAndServices, socialNetwork, function(inArray, data){
        return inArray.endpoint == socialNetwork.name;
      });
      if($scope.providersAndServices[indexOfSocialNetworkInProvidersAndServices] != undefined) {
        $scope.servicesAvailable = [];
        for(var i=0; i < $scope.providersAndServices[indexOfSocialNetworkInProvidersAndServices].services.length; i++) {
          $scope.servicesAvailable.push($unifiedRequestUtils.serverToUnifiedRequest($scope.providersAndServices[indexOfSocialNetworkInProvidersAndServices].services[i]));
        }
      }
    }

    $scope.addService = function(service) {
      $scope.column.unifiedRequests.push(service);
      if($scope.column.title == "") {
        $scope.column.title = service.providerName + " " + service.serviceName;
      }
    }

    $scope.cancelCreateColumn = function() {
      resetView();
      $scope.showModifyColumn = false;
    }

    $scope.deleteService = function(service, arg) {
      var found = false;
      for (var i = 0; !found && i < $scope.column.unifiedRequests.length; i++) {
        if($scope.column.unifiedRequests[i].service == service.service && 
            $scope.column.unifiedRequests[i].args.length > 0) {
          for (var j = 0; !found && j < $scope.column.unifiedRequests[i].args.length; j++) {
            var a = $scope.column.unifiedRequests[i].args[j];
            if(arg.key == a.key && arg.value == a.value) {
              found = true;
              $scope.column.unifiedRequests.splice(i,1);
            }
          }
        }
        else if($scope.column.unifiedRequests[i].service == service.service && arg === undefined) {
          found = true;
          $scope.column.unifiedRequests.splice(i,1);
        }
      }
    }

    $scope.newColumn = function(column) {
      resetView();
      $scope.showModifyColumn = !$scope.showModifyColumn;
      if ($scope.showModifyColumn == true) {
        if($scope.providersAvailable == undefined) {
          $network.send({cmd:"allProviders"});
        }
        if($scope.providersAndServices == undefined) {
          $network.send({cmd:"allUnifiedRequests"});
        }
        if(column !== undefined) {//modif column
          $scope.column = column;
          $scope.column.newColumn = false;
          $scope.column.oldTitle = $scope.column.title;
        } else {//new column
          $scope.column = {};
          $scope.column.newColumn = true;
          $scope.column.title = "";
          $scope.column.unifiedRequests = [];
        }
      }
    };

    $scope.validate = function() {
      var json = "";
      var unifiedRequests = [];
      var column = $scope.column;
      for (var i = 0; i < column.unifiedRequests.length; i++) {
        unifiedRequests.push($unifiedRequestUtils.clientToServerUnifiedRequest(column.unifiedRequests[i]));
      };
      if(column.newColumn == false) {
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
        json = {
          "cmd": "addColumn", 
          "body": {
            "title": column.title,
            "unifiedRequests": unifiedRequests,
            "index": $scope.columnsTitle.length,
            "width": -1,
            "height": -1
          }
        };
      }

      $scope.showErrorBlankArg = checkErrorEmptyArg(column);
      $scope.showErrorDoubleParser = checkErrorDoubleParser(column);
      $scope.showErrorTitleRequired = checkErrorEmptyTitle(column);
      $scope.showErrorTitleAlreadyExist = checkErrorTitleAlreadyExist(column);

      if ($scope.showErrorBlankArg == false && 
          $scope.showErrorDoubleParser == false &&
          $scope.showErrorTitleRequired == false &&
          $scope.showErrorTitleAlreadyExist == false) {
        $scope.showModifyColumn = !$scope.showModifyColumn;
        $network.send(json);
      }
    }

    $scope.deleteColumn = function() {
      $network.send({
        "cmd": "delColumn", 
        "body": {"title": $scope.column.oldTitle}
      });
      $scope.showModifyColumn = false;
    }

    function resetView() {
      $scope.column = undefined;
      $scope.availableSocialNetworksWidth = "90%";
      $scope.servicesAvailable = [];
      $scope.showErrorBlankArg = false;
      $scope.showErrorDoubleParser = false;
      $scope.showErrorTitleRequired = false;
      $scope.showErrorTitleAlreadyExist = false;
      if($scope.providersAvailable != undefined) {
        for (var i = 0; i < $scope.providersAvailable.length; i++) {
          $scope.providersAvailable[i].selected = false;
        };
      }
      if($scope.column !== undefined) {
        $scope.column.title = column.oldTitle;
        for (var i = $scope.column.unifiedRequests.length - 1; i >= 0; i--) {
          if($scope.column.unifiedRequests[i].fromServer == false) {
            $scope.column.unifiedRequests.splice(i, 1);
          }
        };
      }
    }

    // ################################### 
    // # ERRORS CHECK : ALL CHECK ERROR RETURN TRUE IF ERROR 
    // ###################################

    function checkErrorEmptyArg(column) {
      var cleanRegex = /[&\/\\#,+()$~%'":*?<>{}]/g;

      for (var i = 0; i < column.unifiedRequests.length; i++) {
        for (var j = 0; j < column.unifiedRequests[i].args.length; j++) {
          column.unifiedRequests[i].args[j].value = column.unifiedRequests[i].args[j].value.replace(cleanRegex, '');
          if (column.unifiedRequests[i].args[j].value == "") {
            return true;
          }
        };
      };
      return false;
    }

    function checkErrorDoubleParser(column) {
      var nbServiceFound = 0;
      var nbArgFound = 0;
      for (var i = 0; i < column.unifiedRequests.length; i++) {
        nbServiceFound = 0;
        nbArgFound = 0;
        for (var u = 0; u < column.unifiedRequests.length; u++) {
          if(column.unifiedRequests[i].args.length > 0) {
            for (var j = 0; j < column.unifiedRequests[i].args.length; j++) {
              for (var h = 0; h < column.unifiedRequests[u].args.length; h++) {
                if (column.unifiedRequests[i].service == column.unifiedRequests[u].service && 
                  column.unifiedRequests[i].args[j].value == column.unifiedRequests[u].args[h].value) {
                  nbArgFound++;
                }
              };
              if(nbArgFound > 1) {
                return true;
              }
            };
          } else if(column.unifiedRequests[i].service == column.unifiedRequests[u].service) {
            nbServiceFound++;
          }
        };
        if(nbServiceFound > 1) {
          return true;
        }
      };
      return false;
    }

    function checkErrorEmptyTitle(column) {
      return column.title == "";
    }

    function checkErrorTitleAlreadyExist(column) {
      var nb = 0;
      for (var i = 0; i < $scope.columnsTitle.length; i++) {
        if($scope.columnsTitle[i] == column.title) {
          nb++;
          if(column.newColumn == false && nb > 1) {
            return true;
          } else if(column.newColumn == true) {
            return true;
          }
        }
      };
      return false;
    }

}]);

return app;
});
