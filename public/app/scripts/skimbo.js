var skimbo = {}


skimbo.providers = {
	
	display : function() {
		$.ajax({ 
			url: "/api/providers/list", 
			success: function(d) {
				console.log(d);
			}
		});
	}
		
}

skimbo.unified = {

		
	run : function() {
		if (!!window.EventSource) {
			var source = new EventSource('/api/unified2');

			source.addEventListener('message', function(e) {
				console.log(e.data);
			}, false);

			source.addEventListener('open', function(e) {
				console.log("OPEN");
			}, false);

			source.addEventListener('error', function(e) {
				if (e.readyState == EventSource.CLOSED) {
					console.log("CLOSE");
				}
			}, false);

		} else {
			alert("incompatible !");
		}

	}
}