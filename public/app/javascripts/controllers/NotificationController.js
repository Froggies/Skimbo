(function () {

  'use strict';

  angular.module('publicApp').controller('NotificationController', [
    "$scope", "$rootScope", "ArrayUtils", "PopupProvider", "$filter", "DataCache",
    function($scope, $rootScope, $arrayUtils, $popupProvider, $filter, $dataCache) {

      $scope.notifications = [];

      $dataCache.on('tokenInvalid', function(tokensInvalid) {
        var data;
        for (var i = 0; i < tokensInvalid.length; i++) {
          data = {};
          data.providerName = tokensInvalid[i].providerName;
          data.title = $filter('i18n')('DISCONNECT');
          data.footer = $filter('i18n')('CLICK_TO_RECONNECT');
          data.type = "tokenInvalid";
          $scope.notifications.push(data);
        };
        $scope.$apply();
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
             data.type == "Comment" ||
             data.type == "EmailNotSend") {
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
        var data = data || {};
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
        $scope.$apply();
        //if received msg then hide disconnect error (sse has restablish connexion)
        var onMsgOff = $rootScope.$on('msg', function(evt, msg) {
          $scope.closeNotification(data);
          onMsgOff();//remove listener
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

})();