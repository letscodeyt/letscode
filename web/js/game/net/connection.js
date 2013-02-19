define(function(require) {
	require("prototype");
	var MessageDialog = require("game/ui/jq/messagedialog");
	
	return Class.create({
		initialize: function() {
			if (!("WebSocket" in window)) {
				MessageDialog.error(
						"WebSockets are not supported by this browser.");
				this.status = "unsupported";
				return;
			}
			
			this.status = "disconnected";
		},
		
		connect: function() {
			var url = "ws://" + window.location.host + "/clients/";
			console.log("connecting: ", url);
			
			this.socket = new WebSocket(url);
			this.status = "connecting";
			
			// wrap events
			var $this = this;
			
			this.socket.onopen = function(event) {
				$this.onOpen(event);
			};
			
			this.socket.onmessage = function(event) {
				$this.onMessage(event);
			};
			
			this.socket.onclose = function() {
				$this.onClose();
			};
			
			this.socket.onerror = function(error) {
				$this.onError(error);
			};
		},
		
		onOpen: function(event) {
			console.log("connected: ", event);
			this.status = "connected";
		},
		
		onMessage: function(event) {
			var data = JSON.parse(event.data);
			console.log("message: ", data);
			
			if (data.type === "request") {
				
			}
		},
		
		onClose: function() {
			console.log("connection closed");
			this.status = "closed";
		},
		
		onError: function(error) {
			console.log("connection error: " + error);
			this.status = "error: " + error;
		},
		
		send: function(o) {
			if (typeof o === "object") {
				this.socket.send(JSON.stringify(o));
			} else {
				this.socket.send(o.toString());
			}
		}
	});
	
});
