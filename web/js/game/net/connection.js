define(function(require) {
	require("prototype");
	
	var MessageDialog = require("game/ui/jq/messagedialog");
	var AuthenticationResponse = require("./response/authentication");
	
	return Class.create({
		initialize: function() {
			if (!("WebSocket" in window)) {
				MessageDialog.error(
						"WebSockets are not supported by this browser.");
				this.status = "unsupported";
				return;
			}
			
			this.status = "disconnected";
			
			this.messageListeners = [];
			this.initListeners();
		},
		
		initListeners: function() {
			this.messageListener({
				type: "request",
				name: "authenticate",
				callback: function(message) {
					console.log("creating AuthResponse for ", message);
					new AuthenticationResponse(message);
				}
			});
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
			
			// notify all listers, filtering non-matches
			this.messageListeners.forEach(function(l) {
				var notify = true;
				
				for (var prop in l) {
					if (!l.hasOwnProperty(prop)) {
						continue;
					}
					
					if (prop === "callback") {
						continue;
					}
					
					if (!data.hasOwnProperty(prop) || data[prop] !== l[prop]) {
						notify = false;
						console.log("match failed: ", l, data, "prop: " + prop);
						break;
					}
				}
				
				if (notify) {
					l.callback(data);
				}
			}, this);
		},
		
		onClose: function() {
			console.log("connection closed");
			this.status = "closed";
		},
		
		onError: function(error) {
			console.log("connection error: " + error);
			this.status = "error: " + error;
		},
		
		/**
		 * Adds a message listener to be notified of incoming messages. 
		 * Listeners must contain a property 'callback' which will be notified
		 * when a message is a received.
		 * <p>Any additional properties in the listener will be used for 
		 * filtering, such that the callback with only be notified if all extra
		 * listener properties exist on and match those of the incoming
		 * message.</p>
		 * @param {type} listener
		 */
		messageListener: function(listener) {
			this.messageListeners.push(listener);
		},
		
		send: function(o) {
			console.log("sending message typed " + (typeof o) + ": ", o);
			
			if (typeof o === "object") {
				this.socket.send(JSON.stringify(o));
			} else {
				this.socket.send(o.toString());
			}
		}
	});
	
});
