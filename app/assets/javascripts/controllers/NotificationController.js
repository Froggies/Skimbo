'use strict';

controllers.controller('NotificationController', [
  "$scope", "$rootScope", "ArrayUtils", "PopupProvider",
  function($scope, $rootScope, $arrayUtils, $popupProvider) {

    $scope.notifications = [];

    $rootScope.$on('tokenInvalid', function(evt, data) {
      $scope.$apply(function() {
        if(!$arrayUtils.exist($scope.notifications, data, "providerName")) {
          $scope.notifications.push(data);
        }
      });
    });

    $rootScope.$on('newToken', function(evt, data) {
      $scope.$apply(function() {
        var index = $arrayUtils.indexOf($scope.notifications, data, "providerName");
        if(index > -1) {
          $scope.notifications.splice(index, 1);
        }
      });
    });

    $rootScope.$on('error', function(evt, data) {
      $scope.$apply(function() {
        var exist = $arrayUtils.existWith($scope.notifications, data, function(inArray, data) {
          return inArray.providerName == data.providerName && inArray.title == data.msg;
        });
        if(!exist) {
          $scope.notifications.push(data);
        }
      });
    });

    $scope.clickOnNotification = function(notification) {
      if(notification.isError == false) {
        $popupProvider.openPopup({"socialNetwork": notification.providerName});
      } else {
        var index = $arrayUtils.indexOfWith($scope.notifications, notification, function(inArray, data) {
          return inArray.providerName == data.providerName && inArray.msg == data.msg;
        });
        if(index > -1) {
          $scope.notifications.splice(index, 1);
        }
      }
    }

}]);