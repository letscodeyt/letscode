define(function(require) {
	require("prototype");
	var gamejs = require("gamejs");
	
	return Class.create({
		initialize: function() {
			this.components = [];
			this.focusedComponent = null;
		},
		
		addComponent: function(component) {
			this.components.push(component);
			component.set({ parent: this });
		},
		
		setFocus: function(component) {
			if (this.focusedComponent) {
				this.focusedComponent.set({ focused: false});
			}
			
			component.set({ focused: true });
		},
		
		draw: function(surface) {
			surface.clear();
			
			this.drawComponents(surface);
		},
		
		drawComponents: function(surface) {
			var events = gamejs.event.get();
			
			this.components.forEach(function(component) {
				component.draw(surface);
				component.processEvents(events);
			}, this);
		}
	});
});
