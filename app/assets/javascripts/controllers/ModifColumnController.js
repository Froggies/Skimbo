'use strict';

define(["app"], function(app) {

app.controller('ModifColumnController', [
  "$scope", "Network", "$rootScope", "UnifiedRequestUtils", "Visibility", 
  "PopupProvider", "ArrayUtils", "$http", "DataCache",
  function($scope, $network, $rootScope, $unifiedRequestUtils, $visibility, 
    $popupProvider, $arrayUtils, $http, $dataCache) {

    $scope.availableSocialNetworksWidth = "90%";
    $scope.column = undefined;
    $scope.selectedSocialNetwork = undefined;
    $scope.providers = undefined;
    $scope.columnsTitle = [];

    $dataCache.on('allColumns', function(columns) {
      //to check unique title
      $scope.columnsTitle = [];
      for (var i = 0; i < columns.length; i++) {
        $scope.columnsTitle.push(new String(columns[i].title));
      };
    });

    $dataCache.on('allUnifiedRequests', function(providers) {
      var provider, i, j, copy = JSON.parse(JSON.stringify(providers));
      for (i = 0; i < copy.length; i++) {
        provider = copy[i];
        provider.selected = false;
        provider.name = provider.endpoint;
        provider.services = provider.services.slice(0); 
        for (j = 0; j < provider.services.length; j++) {
          provider.services[j] = $unifiedRequestUtils.serverToUnifiedRequest(provider.services[j]);
        };
        if($scope.selectedSocialNetwork != undefined && $scope.selectedSocialNetwork.name == provider.name) {
          $scope.selectedSocialNetwork = provider;
          $scope.selectedSocialNetwork.selected = true;
        }
      };
      $scope.providers = copy;
      $rootScope.$broadcast('loading', {loading: false, translationCode: 'GET_TOKEN_PROGRESS'});
    });

    $rootScope.$on('clientModifyColumn', function(evt, column) {
      $scope.show();
      $scope.column = column;
      $scope.column.newColumn = false;
      $scope.column.oldTitle = $scope.column.title;
    });

    $scope.show = function() {
      resetView();
      $scope.column = {};
      $scope.column.newColumn = true;
      $scope.column.title = "";
      $scope.column.unifiedRequests = [];
    };

    $scope.show();

    $scope.selectSocialNetwork = function(socialNetwork) {
      if($scope.selectedSocialNetwork != undefined) {
        $scope.selectedSocialNetwork.selected = false;
      }
      $scope.selectedSocialNetwork = socialNetwork;
      $scope.selectedSocialNetwork.selected = true;
      $scope.availableSocialNetworksWidth = "";
    }

    $scope.addService = function(service) {
      if(service.hasParser == true) {
        if($scope.selectedSocialNetwork.hasToken == true) {
          addService(service);
        } else {
          $popupProvider.openPopup($scope.selectedSocialNetwork, function() {
            addService(service);
            $network.send({cmd:"allUnifiedRequests"});
            $network.send({cmd:"allPosters"});
          });
        }
      }
    }

    function addService(service) {
      service.fromServer = false;
      $scope.column.unifiedRequests.push(JSON.parse(JSON.stringify(service)));//clone service
      if($scope.column.title == "") {
        $scope.column.title = service.providerName + " " + service.serviceName;
      }
    }

    $scope.selectOracle = function(arg, value) {
      arg.possibleValues = [];
      arg.value = value.call;
    }

    $scope.close = function() {
      $scope.$parent.hide();
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
        $scope.close();
        $network.send(json);
      }
    }

    $scope.deleteColumn = function() {
      $network.send({
        "cmd": "delColumn", 
        "body": {"title": $scope.column.oldTitle}
      });
      $scope.close();
    }

    function resetView() {
      $scope.availableSocialNetworksWidth = "90%";
      if($scope.selectedSocialNetwork != undefined) {
        $scope.selectedSocialNetwork.selected = false;
      }
      $scope.selectedSocialNetwork = undefined;
      $scope.showErrorBlankArg = false;
      $scope.showErrorDoubleParser = false;
      $scope.showErrorTitleRequired = false;
      $scope.showErrorTitleAlreadyExist = false;
      if($scope.column !== undefined) {
        $scope.column.title = $scope.column.oldTitle;
        for (var i = $scope.column.unifiedRequests.length - 1; i >= 0; i--) {
          if($scope.column.unifiedRequests[i].fromServer == false) {
            $scope.column.unifiedRequests.splice(i, 1);
          }
        };
      }
      $scope.column = undefined;
    }

    // ################################### 
    // # ERRORS CHECK : ALL CHECK ERROR RETURN TRUE IF ERROR 
    // ###################################

    var cleanRegex = /[&\\#,+()$~%'":*?<>{}]/g;

    function checkErrorEmptyArg(column) {
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
