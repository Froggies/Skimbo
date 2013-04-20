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
      else {
        var url = image;
        if(image.match("^www")=="www") {
          url = "http://"+image;
        }
        return "/download/"+encodeURIComponent(url);
      }
    }
  }

});

return app;
});