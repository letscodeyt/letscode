define(function(require) {
	var $ = require("jquery");
	require("jquery-ui");
	
	/**
	 * @fileOverview Defines a wrapper class around jQuery UI dialogs. 
	 */
	
	return Class.create({
		initialize: function(opts) {
			// unlike our own UI classes, these options are passed directly to
			// $.dialog(), but we can still provide some defaults
			var options = Object.extend({
				modal: true
			}, opts);
			
			this.dialog = $("<div>");
			this.body(this.dialog);
			this.dialog.dialog(options);
		},
		
		set: function(opts) {
	
		},
		
		/**
		 * Initializes the body for this dialog. Subclasses can append other
		 * HTML elements and fields to the dialog here.
		 * @param {object} e The dialog's root element.
		 */
		body: function(e) {
	
		},
		
		// direct wrapper functions
		
		close: function() {
			this.dialog.dialog("close");
		},
		
		destroy: function() {
			this.dialog.dialog("destroy");
		},
		
		isOpen: function() {
			return this.dialog.dialog("isOpen");
		},
		
		moveToTop: function() {
			this.dialog.dialog("moveToTop");
		},
		
		open: function() {
			this.dialog.dialog("open");
		},
		
		option: function() {
	
		},
		
		widget: function() {
			return this.dialog.dialog("widget");
		},
		
		on: function(event, callback) {
			this.dialog.on(event, callback);
		}
		
	});
});
