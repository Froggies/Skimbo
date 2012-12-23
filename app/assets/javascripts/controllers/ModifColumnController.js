'use strict';

controllers.controller('ModifColumnController', [
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
            $scope.serviceProposes = serviceProposes;
          });
        }
        else {
          $scope.serviceProposes = serviceProposes;
        }
    });

    $scope.newColumn = function(column) {
      $scope.showModifyColumn = !$scope.showModifyColumn;
      if ($scope.showModifyColumn == true) {
        if($scope.serviceProposes == undefined) {
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