'use strict';

define(["app"], function(app) {

  app.directive('toggleClick', [function() {

    return function (scope, element) {
      element.addClass('icon-play');
      var ul = element.parent().find('ul');
      ul.css('display', 'none');
      element.bind('click', function() {
      	if(ul.css('display') == 'none') {
      		ul.css('display', 'block');
          element.addClass('icon-bottom');
          element.removeClass('icon-play');
      	} else {
      		ul.css('display', 'none');
          element.addClass('icon-play');
          element.removeClass('icon-bottom');
      	}
      });
      element.bind('$destroy', function() {
        ul = undefined;
      	element.unbind('click');
      	element.unbind('$destroy');
      })
    };

  }]);
});
