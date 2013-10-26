(function () {

	'use strict';

  angular.module('publicApp').directive('focus', [function() {

    return function (scope, element) {
      element[0].focus();
    };

  }]);

})();

