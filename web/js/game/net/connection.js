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
			this.statusListeners = [];
			this.initListeners();
		},
		
		/**
		 * Initializes some essential MessageListeners, including basic handlers
		 * for authentication.
		 */
		initListeners: function() {
			/*this.messageListener({
				type: "request",
				name: "authenticate",
				callback: function(message) {
					console.log("creating AuthResponse for ", message);
					new AuthenticationResponse(message);
				}
			});*/
			
			this.messageListener({
				type: "notification",
				callback: function(message) {
					console.log("notification: " + message);
					new MessageDialog({
						type: message.class,
						text: message.message
					});
				}
			});
			
			this.messageListener({
				type: "chat",
				callback: function(message) {
					console.log("chat message: ", message);
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
			
			// notify all listers, filtering fields that don't match
			// start by iterating over each listener...
			this.messageListeners.forEach(function(l) {
				var notify = true;
				
				// then look at every property in the listener
				for (var prop in l) {
					// skip inherited properties
					if (!l.hasOwnProperty(prop)) {
						continue;
					}
					
					// skip the 'callback' field
					if (prop === "callback") {
						continue;
					}
					
					// if the listener's property doesn't exist, or doesn't
					// match, the match fails
					if (!data.hasOwnProperty(prop) || data[prop] !== l[prop]) {
						notify = false;
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
		 * @param {function} listener
		 */
		messageListener: function(listener) {
			this.messageListeners.push(listener);
		},
		
		/**
		 * Adds the given function to the list of callbacks to be executed when
		 * a status change event has occurred. Specifically, this refers to the
		 * value of the <code>status</code> field in this 
		 * @param {function} listener the callback function to execute when the
		 *     even has been triggered
		 */
		statusListener: function(listener) {
			this.statusListeners.push(listener);
		},
		
		/**
		 * Sets the current connection status. This is only intended for
		 * internal use; to monitor the status field for changes use the
		 * <code>statusListener</code> function to add a callback.
		 * @param {string} status the new status string
		 */
		setStatus: function(status) {
			var oldStatus = this.status;
			this.status = status;
			
			this.statusListeners.forEach(function(l) {
				l(status, oldStatus);
			}, this);
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
