define(function(require) {
	require("prototype");
	
	var Message = require("./message");
	
	var RequestMessage = Class.create(Message, {
		initialize: function($super, name, data) {
			this.name = name;
			this.data = data;
			
			// generate a uuid, via stackoverflow 
			// http://stackoverflow.com/a/105078
			var id = '';
			for (var i = 0; i < 32; i++) {
				var c = Math.floor(Math.random() * 16)
						.toString(16)
						.toUpperCase();
				id += c;
			}
			
			this.id = id;
			
			this.listeners = [];
			
			console.log("message id is " + id);
		},
		
		listener: function(callback) {
			this.listeners.push(callback);
		},
		
		handleResponse: function(resp) {
			var $this = this;
			
			this.listeners.forEach(function(callback) {
				callback({
					request: $this,
					response: resp,
					name: resp.name,
					data: resp.data
				});
			});
		},
		
		onResponseReceived: function() {
			// can be overridden by subclasses
		},
		
		serialize: function() {
			return JSON.stringify({
				id: this.id,
				type: "request",
				name: this.name,
				data: this.data
			});
		}
	});
	
	return RequestMessage;
});

