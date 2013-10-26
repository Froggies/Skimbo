(function () {
  'use strict';

  angular.module('publicApp').controller('GlassController', ["$scope", "$rootScope", function($scope, $rootScope) {

    var _optionalCallback = undefined;
    $scope.showView = false;

    $rootScope.$on('glassShowView', function(evt, view, optionalCallback) {
      _optionalCallback = optionalCallback;
      if(view == 'modifColumn') {
        show('views/modifColumn.html');
      } else if(view == 'help') {
        var lang;
        if($rootScope.currentLanguage === 'fr') {
          lang = 'fr';
        } else {
          lang = 'en';
        }
        show('views/help_'+lang+'.html');
      } else {
        show(view);
      }
    });

    function show(view) {
      if(view == $scope.showView) {
        $scope.hide();
      } else {
        $scope.showView = view;
      }
    }

    $scope.hide = function() {
      $scope.showView = false;
    }

    $scope.loaded = function() {
      if(_optionalCallback !== undefined) {
        _optionalCallback();
      }
      _optionalCallback = undefined;
    }

  }]);
})();

