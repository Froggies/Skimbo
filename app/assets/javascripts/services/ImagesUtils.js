'use strict';

define(["app"], function(app) {

app.factory("ImagesUtils", function() {

  var defaultImage = "/assets/img/image-default.png";

  return {
    isDefaultImage: function(image) {
      return image == defaultImage;
    },

    checkExistingImage: function(image) {
      if(image == "" || image == undefined) {
        return defaultImage;
      }
      else {
        var url = image;
        if(image.match("^www")=="www") {
          url = "http://"+image;
        }
        return "/download?url="+encodeURIComponent(url);
      }
    },

    defaultImage: function() {
      return defaultImage;
    }
  }

});

return app;
});