define(function(require) {
	var $ = require("jquery");
	
	/**
	 * @fileOverview Controls the chat box
	 */
	
	var ChatBox = Class.create({
		initialize: function(opts) {
			var options = Object.extend({
				historySize: 10,
				scrollMode: "smart", // one of: smart, always, never
				textField: $("#chat-field"),
				sendButton: $("#chat-send"),
				log: $("#chat-log")
			}, opts);
			
			if (!options.connection) {
				throw "ChatBox must be passed a Connection instance via the "
						+ "'connection' parameter.";
			}
			
			this.set(options);
			
			var $this = this;
			this.connection.messageListener({
				type: "chat",
				callback: function(message) {
					console.log("chatbox got message");
					$this.addMessage(message.text);
				}
			});
			
			// initial message count
			this.messageCount = 0;
		},
		
		set: function(props) {
			var $this = this;
			
			// update the send button handler if needed
			if (props.sendButton) {
				// remove the click handler from the old button (if any)
				if (this.sendButton && this.sendButtonHandler) {
					this.sendButton.unbind('click', this.sendButtonHandler);
				}
				
				// scoping derpiness, we need a reference to 'this'
				this.sendButtonHandler = function() {
					$this.doSend();
				};
				
				// add the new handler
				props.sendButton.bind('click', this.sendButtonHandler);
			}
			
			// update text field handler as needed
			if (props.textField) {
				if (this.textField && this.keypressHandler) {
					this.textField.unbind('keypress', this.keypressHandler);
				}
				
				this.keypressHandler = function() {
					if (event.which === 13) {
						$this.doSend();
					}
				};
				
				props.textField.bind('keypress', this.keypressHandler);
			}
	
			// we don't have to do anything special, just copy blindly
			for (p in props) {
				this[p] = props[p];
			}
		},
		
		/**
		 * Performs a message send based on the current text field value.
		 */
		doSend: function() {
			var text = this.textField.val();
			
			var game = require("game/game").getInstance();
			game.connection.send({
				type: "chat",
				text: text
			});
			
			this.textField.val("");
		},
		
		/**
		 * Appends the given string to the chat log container.
		 * @param message the message to add to the field
		 */
		addMessage: function(message) {
			// determine before we add the message whether or not scrolling
			// should take place (not dependent on the scroll mode)
			var scroll =
					(this.log.prop("scrollHeight") - this.log.scrollTop()
					=== this.log.outerHeight());
			
			// remove first if we've filled the history
			if (this.messageCount >= this.historySize) {
				this.log.find("p:first").remove();
			} else {
				this.messageCount++;
			}
			
			// add the message
			$("<p>", {
				text: message
			}).appendTo(this.log);
			
			// scroll to the bottom, depending on the scroll mode
			if ((scroll && this.scrollMode === "smart")
					|| this.scrollMode === "always") {
				this.scrollToBottom();
			}
		},
		
		/**
		 * Scrolls the text field to the bottom of the chat box
		 */
		scrollToBottom: function() {
			this.log.animate({
				scrollTop: this.log.prop("scrollHeight")
			}, 500);
		}
	});
	
	return ChatBox;
});

