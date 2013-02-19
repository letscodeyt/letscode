define(function(require) {
	require("prototype");
	var $ = require("jquery");
	var Dialog = require("./dialog");
	
	/**
	 * @fileOverview Defines an extension of the Dialog class that displays a
	 * message.
	 */
	
	var MessageDialog = Class.create(Dialog, {
		initialize: function($super, opts) {
			var $this = this;
			
			var options = Object.extend({
				text: "",
				type: "info",
				icon: null,
				buttons: [{
					text: "Close",
					click: function() {
						$this.close();
					}
				}]
			}, opts);
			
			// some predefined icons
			var iconMap = {
				info:    "res/ui/dialog-information.png",
				warning: "res/ui/dialog-warning.png",
				error:   "res/ui/dialog-error.png"
			};
			
			// check for options and remove them if needed
			
			if (typeof options.text !== 'undefined') {
				this.text = options.text;
				delete options.text;
			}
			
			if (typeof options.type !== 'undefined') {
				this.icon = iconMap[options.type];
			}
			
			if (options.icon) {
				this.icon = options.icon;
			}
			
			$super(options);
		},
		
		body: function(e) {
			var table = $("<table>");
			table.css("width", "100%");
			
			var tr = $("<tr>");
			
			if (this.icon) {
				var iconCol = $("<td>");
				iconCol.css("width", "64px");
				iconCol.css("padding", "5px");
				
				var icon = $("<img>");
				icon.attr("src", this.icon);
				iconCol.append(icon);
				
				tr.append(iconCol);
			}
			
			var td = $("<td>");
			td.text(this.text);
			tr.append(td);
			
			table.append(tr);
			e.append(table);
		}
		
	});
	
	MessageDialog.show = function() {
		var title = "";
		var text = "";
		var type = "";
		
		if (arguments.length === 3) {
			type  = arguments[0];
			text  = arguments[1];
			title = arguments[2];
		} else if (arguments.length === 2) {
			title = arguments[0];
			text  = arguments[1];
		} else if (arguments.length === 1) {
			text  = arguments[0];
		}
		
		new MessageDialog({type: type, text: text, title: title });
	};
	
	MessageDialog.info = function() {
		var title = "Information";
		var text = "";
		
		if (arguments.length === 1) {
			text = arguments[0];
		} else if (arguments.length === 2) {
			title = arguments[0];
			text = arguments[1];
		}
		
		MessageDialog.show("info", text, title);
	};
	
	MessageDialog.warning = function() {
		var title = "Warning";
		var text = "";
		
		if (arguments.length === 1) {
			text = arguments[0];
		} else if (arguments.length === 2) {
			title = arguments[0];
			text = arguments[1];
		}
		
		MessageDialog.show("warning", text, title);
	};
	
	MessageDialog.error = function() {
		var title = "Error";
		var text = "";
		
		if (arguments.length === 1) {
			text = arguments[0];
		} else if (arguments.length === 2) {
			title = arguments[0];
			text = arguments[1];
		}
		
		MessageDialog.show("error", text, title);
	};
	
	return MessageDialog;
	
});

