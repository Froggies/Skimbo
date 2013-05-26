'use strict';

define(["app"], function(app) {

app.controller('MediasController', [
  "$scope", "$rootScope", "ImagesUtils", "$timeout",
  function($scope, $rootScope, $imagesUtils, $timeout) {

    $scope.display = false;
    $scope.urlImage = $imagesUtils.defaultImage();
    $scope.images = [];
    $scope.loading = true;

    $rootScope.$on('displayMedias', function(evt, message) {
      console.log(message);
      
      var pictures;
      if(message.picturesMax && message.picturesMax.length > 0) {
        pictures = message.picturesMax;
      } else {
        pictures = message.picturesMin;
      }
      
      $scope.urlImage = $imagesUtils.checkExistingImage(pictures[0]);
      $scope.images = [];
      if(message.picturesMin.length > 1) {
        for (var i = 0; i < message.picturesMin.length; i++) {
          $scope.images.push($imagesUtils.checkExistingImage(message.picturesMin[i]));
        };
      }
      $scope.display = true;
      $scope.loading = true;
      $timeout(function() {
        $scope.loading = false;
      }, 3000);
    });

    $scope.selectImage = function(urlImage) {
      $scope.urlImage = urlImage;
      $scope.loading = true;
      $timeout(function() {
        $scope.loading = false;
      }, 2000);
    }

    $scope.close = function() {
      $scope.display = false;
    }

  }]);
});