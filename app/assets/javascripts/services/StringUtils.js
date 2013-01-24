'use strict';

define(["app"], function(app) {

app.factory("StringUtils", function() {

  var urlexp = /(http|ftp|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\.,@?^=%&amp;:/~\+#]*[\w\-\@?^=%&amp;/~\+#])?/gi;

	return {

		urlify: function(msg) {
			var text = msg.message;
			return text.replace(urlexp, function(url) {
  			return '<a class="link-into-message '+msg.from+'" href="' + url + '" target="_blank">∞</a>';
			});
		},

		truncateString: function(str) {
		  if(str.length > 140 && !urlexp.test(str)) { 
		    return String(str).substring(0, 140)+"...";
		  } else {
		    return str;
		  }
		},

		serviceHasTypeChar: function(service) {
      if(service.typeServiceChar != "") {
        return true;
      }
      return false;
    },

    typeServiceCharByService: function(service) {
      var socialNetworkName = service.split(".")[0];
      var typeService = service.split(".")[1];
        if(typeService == "group") {
          return "ഹ";
        }
        else if(typeService == "user") {
          if(socialNetworkName == "twitter") {
            return "@";
          }
          else {
            return "😊";
          }
        }
        else if (typeService == "hashtag") {
          return "#";
        }
        return "";
    }

	};

});

return app;
});