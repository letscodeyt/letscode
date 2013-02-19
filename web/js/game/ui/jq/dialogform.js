define(function(require) {
	require("prototype");
	var $ = require("jquery");
	var Dialog = require("./dialog");
	
	/**
	 * @fileOverview Defines an extension of the Dialog class that displays a
	 * form.
	 */
	
	return Class.create(Dialog, {
		initialize: function($super, opts) {
			var options = Object.extend({
				
			}, opts);
			
			// fields shouldn't be passed to jqueryui
			if (options.fields) {
				this.fields = options.fields;
				delete options.fields;
			}
			
			$super(options);
		},
		
		body: function(e) {
			this.form = $("<form>");
			
			for (var labelName in this.fields) {
				if (!this.fields.hasOwnProperty(labelName)) {
					// skip any inherited members
					continue;
				}
				
				// make the label
				var label = $("<label>");
				label.html(labelName);
				label.css("display", "block");
				
				var attrs = this.fields[labelName];
				if (attrs.name) {
					label.attr("for", attrs.name);
				}
				
				this.form.append(label);
				
				// create the form element
				var input = $("<input>");
				input.addClass("ui-corner-all"); // make the fields look nice
				input.addClass("ui-widget-content");
				input.css("width", "95%");
				for (var attr in attrs) {
					if (attrs.hasOwnProperty(attr)) {
						input.attr(attr, attrs[attr]);
					}
				}
				this.form.append(input);
			}
			
			e.append(this.form);
		},
		
		/**
		 * Gets the value of the field with the given name.
		 * @param {type} name the name of the field to get
		 * @returns {object} the given field value
		 */
		get: function(name) {
			return this.form.children("input[name=" + name + "]").first().val();
		},
		
		set: function(name, value) {
			this.form.children("input[name=" + name + "]").val(value);
		}
	});
	
});
