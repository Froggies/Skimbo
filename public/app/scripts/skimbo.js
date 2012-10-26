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