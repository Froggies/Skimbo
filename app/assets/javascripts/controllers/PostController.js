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
        for (var i = 0; i < providers.length; i++) {
          var provider = providers[i];
          provider.selected = false;
          if(provider.name == "linkedin" || provider.name == "github" ||
             provider.name == "scoopit" || provider.name == "viadeo") {
            $scope.providersWithTitle.push(provider);
          }
          if(provider.name == "linkedin" || provider.name == "facebook" ||
             provider.name == "scoopit" || provider.name == "viadeo") {
            $scope.providersWithUrl.push(provider);
          }
          if(provider.name == "linkedin" || provider.name == "facebook" ||
             provider.name == "scoopit" || provider.name == "viadeo") {
            $scope.providersWithImage.push(provider);
          }
        }
      });
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
      console.log(poster);
      if(poster.hasToken == true) {
        poster.selected = !poster.selected;
      } else {
        $popupProvider.openPopup(poster, function() {
          poster.selected = !poster.selected;
        });
      }
      
    }

    $scope.post = function() {
      var selectedProviders = [];
      for (var i = 0; i < $scope.providers.length; i++) {
        var p = $scope.providers[i];
        if(p.selected == true) {
          selectedProviders.push(p.name);
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
        $network.send(o);
        console.log(o);
        $scope.showPost = false;
        resetView();
      }
    };

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