define(function(require) {
	require("prototype");
	
	/**
	 * @fileOverview Defines a simple layout manager for Components
	 */
	
	return Class.create({
		
		/**
		 * Creates a new Aligner. Required properties: bounds, components
		 * @param {Object} opts options to override defaults
		 */
		initialize: function(opts) {
			var options = Object.extend({
				mode: "vertical",
				position: 'center',
				padding: 10
			}, opts);
			
			this.set(options);
		},
		
		set: function(props) {
			if (props.bounds) {
				this.bounds = props.bounds;
			}
			
			if (props.components) {
				this.components = props.components;
			}
			
			if (props.mode) {
				this.mode = props.mode;
			}
			
			if (props.position) {
				this.position = props.position;
			}
			
			if (props.padding) {
				this.padding = props.padding;
			}
		},
		
		align: function() {
			if (this.mode === "vertical") {
				// find total height of all elements
				var totalHeight = 0;
				this.components.forEach(function(component) {
					totalHeight += component.height();
				}, this);
				
				// add element padding
				var paddingHeight = (this.components.length - 1) * this.padding;
				totalHeight += paddingHeight;
				
				// check if the elements will fit, if not, force a resize
				var forcedSize = null;
				if (totalHeight > this.bounds.height) {
					// divide the available height equally (minus padding)
					forcedSize = (this.bounds.height - paddingHeight) /
							this.components.length;
					
					// reset the changed total height
					totalHeight = forcedSize * this.components.length
							+ paddingHeight;
				}
				
				// pick the starting position
				var startY;
				switch (this.position) {
					case 'leading':
					case 'beginning':
					case 'start':
					case 'top':
						startY = 0;
						break;
						
					case 'center':
						startY = (this.bounds.height / 2) - (totalHeight / 2);
						break;
					
					case 'trailing':
					case 'end':
					case 'bottom':
						startY = this.bounds.height - totalHeight;
						break;
					default:
						startY = 0;
				}
				
				// position the elements
				var currentY = startY;
				
				this.components.forEach(function(component) {
					component.x(this.bounds.x);
					component.y(currentY);
					
					component.width(this.bounds.width);
					// only force a consistent height if needed
					if (forcedSize) {
						component.height(forcedSize);
					}
					
					currentY += component.height() + this.padding;
				}, this);
			}
		}
	});
});
