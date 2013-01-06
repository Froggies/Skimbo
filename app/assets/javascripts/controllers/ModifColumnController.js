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

    $rootScope.$on('availableServices', function(evt, serviceProposes) {
      if(!$scope.$$phase) {
          $scope.$apply(function() {
            $scope.socialNetworks = serviceProposes;
          });
        }
        else {
          $scope.socialNetworks = serviceProposes;
        }
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

    $scope.addSocialNetwork = function(socialNetwork) {
      if($scope.socialNetworksSelected == undefined) {
        $scope.socialNetworksSelected = [];
        $scope.socialNetworksSelected.push(socialNetwork);
      }
      else if(! $arrayUtils.exist($scope.socialNetworksSelected,socialNetwork, "name")) {
        $scope.socialNetworksSelected.push(socialNetwork);
      }
      $scope.selectSocialNetwork(socialNetwork);
    }

    $scope.selectSocialNetwork = function(socialNetwork) {
      var indexOfLastSelected = $arrayUtils.indexOfWith($scope.socialNetworksSelected,undefined, function(inArray, data) {
          return inArray.selected == true;
      });
      if($scope.socialNetworksSelected[indexOfLastSelected] != undefined) {
        if($scope.socialNetworksSelected[indexOfLastSelected] != socialNetwork) {
          $scope.socialNetworksSelected[indexOfLastSelected].selected = false;
        }
      }
      var indexOfSocialNetwork = $arrayUtils.indexOf($scope.socialNetworksSelected,socialNetwork, "name");
      $scope.socialNetworksSelected[indexOfSocialNetwork].selected = true;
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
      if($scope.servicesInColumn == undefined) {
        $scope.servicesInColumn = [];
      }
      $scope.servicesInColumn.push(service);
    }

    $scope.cancelCreateColumn = function() {
      $scope.socialNetworksSelected = [];
      $scope.servicesAvailable = [];
      $scope.servicesInColumn = [];
    }

    $scope.deleteService = function(service, arg) {
      var found = false;
      for (var i = 0; !found && i < $scope.servicesInColumn.length; i++) {
        if($scope.servicesInColumn[i].service == service.service && 
            $scope.servicesInColumn[i].args.length > 0) {
          for (var j = 0; !found && j < $scope.servicesInColumn[i].args.length; j++) {
            var a = $scope.servicesInColumn[i].args[j];
            if(arg.key == a.key && arg.value == a.value) {
              found = true;
              $scope.servicesInColumn.splice(i,1);
            }
          }
        }
        else if($scope.servicesInColumn[i].service == service.service && arg === undefined) {
          found = true;
          $scope.servicesInColumn.splice(i,1);
        }
      }
    }

    $scope.newColumn = function(column) {
      $scope.showModifyColumn = !$scope.showModifyColumn;
      if ($scope.showModifyColumn == true) {
        if($scope.socialNetworks == undefined) {
          $network.send({cmd:"allUnifiedRequests"});
        }
        if($scope.providersAvailable == undefined) {
          $network.send({cmd:"allProviders"});
        }
        if($scope.providersAndServices == undefined) {
          $network.send({cmd:"allUnifiedRequests"});
        }
      }
      if($scope.showModifyColumn == true) {
        //column.oldTitle = column.title;
      } else {
        //column.title = column.oldTitle;
        // column.showErrorBlankArg = false;
        // for (var i = column.unifiedRequests.length - 1; i >= 0; i--) {
        //   if(column.unifiedRequests[i].fromServer == false) {
        //     column.unifiedRequests.splice(i, 1);
        //   }
        // };
      }
    };

  }

]);

return app;
});
