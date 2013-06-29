'use strict';

define(["app"], function(app) {

app.controller('PostMessageController', [
  "$scope", "Network", "$rootScope", "PopupProvider",
  function($scope, $network, $rootScope, $popupProvider) {

    $scope.showHelp = false;
    $scope.maxLength = 140;
    resetView();

    $rootScope.$on('allPosters', function(evt, providers) {
      $scope.$apply(function() {
        $scope.providersWithTitle = [];
        $scope.providersWithUrl = [];
        $scope.providersWithImage = [];
        $scope.providers = providers;
        for (var i = 0; i < $scope.providers.length; i++) {
          var provider = $scope.providers[i];
          provider.name = provider.service;
          provider.selected = false;
          if(provider.service == "linkedin" || provider.service == "github" ||
             provider.service == "scoopit" || provider.service == "viadeo" ||
             provider.service == "googleplus") {
            $scope.providersWithTitle.push(provider);
          }
          if(provider.service == "linkedin" || provider.service == "facebook" ||
             provider.service == "scoopit" || provider.service == "viadeo") {
            $scope.providersWithUrl.push(provider);
          }
          if(provider.service == "linkedin" || provider.service == "facebook" ||
             provider.service == "scoopit" || provider.service == "viadeo" ||
             provider.service == "googleplus") {
            $scope.providersWithImage.push(provider);
          }
          if(provider.canHavePageId == true) {
            provider.arg = {};
            provider.arg.possibleValues = [];
          }
        }
        console.log($scope.providers);
        $rootScope.$broadcast('loading', {loading: false, translationCode: 'GET_TOKEN_PROGRESS'});
      });
    });

    $rootScope.$on('dispatchMsg', function(evt, message) {
      resetView();
      $scope.title = "";
      $scope.message = message.original;
      $scope.url = message.directLink;
      $scope.image = "";
    });

    $scope.selectPoster = function(poster) {
      if(poster.hasToken == true) {
        poster.selected = !poster.selected;
        for (var i = 0; i < $scope.providersWithTitle.length; i++) {
          if($scope.providersWithTitle[i].name == poster.name) {
            $scope.showTitleInput = true;
            return;
          }
        };
      } else {
        $popupProvider.openPopup({name:poster.tokenProvider}, function() {
          poster.selected = !poster.selected;
          $network.send({cmd:"allUnifiedRequests"});
          $network.send({cmd:"allPosters"});
        });
      }

    }

    $scope.post = function() {
      var selectedProviders = [];
      for (var i = 0; i < $scope.providers.length; i++) {
        var p = $scope.providers[i];
        if(p.selected == true) {
          console.log(p);
          name = p.name || p.service;
          selectedProviders.push({name: name, toPageId: p.toPageId});
        }
      };

      //check errors
      if(selectedProviders.length == 0) {
        $scope.showErrorPosterRequired = true;
        $scope.showPosters = true;
      /*} else if($scope.title == "") {
        $scope.showErrorPosterRequired = false;
        $scope.showErrorTitleRequired = true;
      */} else if($scope.message == "") {
        $scope.showErrorPosterRequired = false;
        $scope.showErrorTitleRequired = false;
        $scope.showErrorContentRequired = true;
      } else if($scope.isDelayedPost == true && (
          $scope.timeToPost == "" || $scope.timeToPost == undefined ||
          !moment($scope.timeToPost, "DD-MM-YYYY HH:mm").isValid())) {
        $scope.showErrorPosterRequired = false;
        $scope.showErrorTitleRequired = false;
        $scope.showErrorContentRequired = false;
        $scope.showErrorBadDate = true;
      } else {
        var o = {
          cmd: "post",
          body: {
            providers:selectedProviders,
            post: {
              title: $scope.title,
              message: $scope.message,
              url: $scope.url,
              url_image: $scope.image
            }
          }
        }
        if($scope.isDelayedPost == true) {
          o.cmd = "delayedPost";
          o.body.timeToPost = moment.utc(moment($scope.timeToPost, "DD-MM-YYYY HH:mm")).valueOf();
        }
        $network.send(o);
        $scope.showPost = false;
        resetView();
      }
    };

    $scope.selectOracle = function (provider, val) {
      provider.toPageId = val.call;
      provider.possibleValues = [];
    }

    $scope.close = function() {
      $scope.$parent.hide();
    }

    function resetView() {
      if($scope.providers == undefined) {
        $network.send({cmd:"allPosters"});
      }
      $scope.title = "";
      $scope.message = "";
      $scope.url = "";
      $scope.image = "";
      $scope.showErrorPosterRequired = false;
      $scope.showErrorTitleRequired = false;
      $scope.showErrorContentRequired = false;
      $scope.showErrorBadDate = false;
      $scope.isDelayedPost = false;
      $scope.timeToPost = "";
      $scope.showLinkInput = false;
      $scope.showImageInput = false;
    }

  }]);
});
