define(function(require) {
	require("prototype");
	
	var Message = Class.create({
		initialize: function(data) {
			this.data = data;
		},
		
		serialize: function() {
			return JSON.stringify(this.data);
		}
	});
	
	return Message;
});
