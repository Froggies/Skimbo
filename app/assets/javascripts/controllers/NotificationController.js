'use strict';

define(["app"], function(app) {

app.controller('NotificationController', [
  "$scope", "$rootScope", "ArrayUtils", "PopupProvider", "$filter",
  function($scope, $rootScope, $arrayUtils, $popupProvider, $filter) {

    $scope.notifications = [];

    $rootScope.$on('tokenInvalid', function(evt, data) {
      $scope.$apply(function() {
        data.title = $filter('i18n')('DISCONNECT');
        data.footer = $filter('i18n')('CLICK_TO_RECONNECT');
        data.type = "tokenInvalid";
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
        if(data.type == "RateLimit" ||
           data.type == "Timeout" ||
           data.type == "Unknown" ||
           data.type == "Parser" ||
           data.type == "NoParser" ||
           data.type == "Post" ||
           data.type == "Star" ||
           data.type == "Comment") {
          data.title = $filter('i18n')(data.type);
        } else {
          data.title = "UNKNOW ERROR TYPE "+data.type;
        }
        data.footer = $filter('i18n')('CLICK_TO_HIDE');
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
        data.title = $filter('i18n')('DISCONNECT');
        data.footer = $filter('i18n')('CLICK_TO_RECONNECT');
        data.type = "disconnect";
        data.providerName = "skimbo";
        var exist = $arrayUtils.existWith($scope.notifications, data, function(inArray, data) {
          return inArray.providerName == data.providerName && inArray.title == data.title;
        });
        if(!exist) {
          $scope.notifications.push(data);
        }
      });
    });

    $scope.clickOnNotification = function(notification) {
      if(notification.type == "tokenInvalid") {
        $popupProvider.openPopup({"name": notification.providerName});
      } else if(notification.type == "disconnect") {
        if(notification.forceIdentification == true) {
          window.location.href = "/logout";
        } else {
          window.location.href = "/";
        }
      } else {
        var index = $arrayUtils.indexOfWith($scope.notifications, notification, function(inArray, data) {
          return inArray.providerName == data.providerName && inArray.msg == data.msg;
        });
        if(index > -1) {
          $scope.notifications.splice(index, 1);
        }
      }
    }

    $scope.closeNotification = function(notification) {
      var index = $arrayUtils.indexOfWith($scope.notifications, notification, function(inArray, data) {
        return inArray.providerName == data.providerName && inArray.msg == data.msg;
      });
      if(index > -1) {
        $scope.notifications.splice(index, 1);
      }
    }

}]);

return app;
});