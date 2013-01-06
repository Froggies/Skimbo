'use strict';

define(["app"], function(app) {

app.controller('NotificationController', [
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
          return inArray.providerName == data.providerName && inArray.title == data.title;
        });
        if(!exist) {
          $scope.notifications.push(data);
        }
      });
    });

    $rootScope.$on('disconnect', function(evt, data) {
      $scope.$apply(function() {
        data.providerName = "skimbo";
        $scope.notifications.push(data);
      });
    });

    $scope.clickOnNotification = function(notification) {
      if(notification.isError == false && notification.providerName != "skimbo") {
        $popupProvider.openPopup({"socialNetwork": notification.providerName});
      } else if(notification.isError == false && notification.providerName == "skimbo") {
        window.location.href = "/";
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

return app;
});