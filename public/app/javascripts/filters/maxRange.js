'use strict';

define(["app"], function(app) {

app.filter('maxRange', function() {
  return function(input, total) {
  	if(input) {
      total = parseInt(total);
      var res = [];
      for (var i=0; i<total && i<input.length; i++)
        res.push(input[i]);
      return res;
    }
  };
});

});