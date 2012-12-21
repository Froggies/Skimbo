services.factory("StringUtils", ["$http", function($http) {

	return {

		urlexp: /(http|ftp|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\.,@?^=%&amp;:/~\+#]*[\w\-\@?^=%&amp;/~\+#])?/gi,

		urlify: function(msg) {
			var text = msg.message;
  			return text.replace(this.urlexp, function(url) {
    			return '<a class="link-into-message '+msg.from+'" href="' + url + '" target="_blank">∞</a>';
  			});
		},

		truncateString: function(str) {
		  if(str.length > 140 && !this.urlexp.test(str)) { 
		    return String(str).substring(0, 140)+"...";
		  } else {
		    return str;
		  }
		}

	};

}]);