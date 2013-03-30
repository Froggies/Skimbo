'use strict';

define(["app"], function(app) {

app.controller('PostMessageController', [
  "$scope", "Network", "$rootScope", "PopupProvider",
  function($scope, $network, $rootScope, $popupProvider) {

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

    $scope.showPost = false;
    $scope.showHelp = false;
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
        console.log($scope.providersWithTitle);
      });
    });

    $rootScope.$on('dispatchMsg', function(evt, message) {
      resetView();
      $scope.show();
      $scope.showPost = true;//force show
      $scope.title = "";
      $scope.message = message.original;
      $scope.url = message.directLink;
      $scope.image = "";
    });

    $scope.show = function() {
      $scope.showPost = !$scope.showPost;
      if ($scope.showPost == true) {
        if($scope.providers == undefined) {
          $network.send({cmd:"allPosters"});
        }
      }
    };

    $scope.selectPoster = function(poster) {
      if(poster.hasToken == true) {
        poster.selected = !poster.selected;
      } else {
        $popupProvider.openPopup(poster, function() {
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
      } else if($scope.title == "") {
        $scope.showErrorPosterRequired = false;
        $scope.showErrorTitleRequired = true;
      } else if($scope.message == "") {
        $scope.showErrorPosterRequired = false;
        $scope.showErrorTitleRequired = false;
        $scope.showErrorContentRequired = true;
      } else {
        var o = {
          cmd:"post",
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
        console.log(o);
        $network.send(o);
        $scope.showPost = false;
        resetView();
      }
    };

    $scope.selectOracle = function (provider, val) {
      provider.toPageId = val.call;
      provider.possibleValues = [];
    }

    function resetView() {
      $scope.title = "";
      $scope.message = "";
      $scope.url = "";
      $scope.image = "";
      $scope.showErrorPosterRequired = false;
      $scope.showErrorTitleRequired = false;
      $scope.showErrorContentRequired = false;
    }

  }]);
});