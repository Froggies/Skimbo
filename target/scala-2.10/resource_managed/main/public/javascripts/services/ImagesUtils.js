'use strict';

define(["app"], function(app) {

app.factory("ImagesUtils", function() {

  return {
    isDefaultImage: function(image) {
      return image == "/assets/img/image-default.png";
    },

    checkExistingImage: function(image) {
      if(image == "" || image == undefined) {
        return "/assets/img/image-default.png";
      }
      else if(image.match("^www")=="www") {
        return "http://"+image;
      }
      else {
        return image;
      }
    }
  }

});

return app;
});