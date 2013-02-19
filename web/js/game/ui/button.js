define(function(require) {
	require("prototype");
	var gamejs = require("gamejs");
	var Component = require("game/ui/component");
	var Color = require("game/ui/color");
	
	/**
	 * @fileoverview Defines a Button class that has some text and handles mouse
	 * click events.
	 */
	
	// create a subclass of Component (Button)
	return Class.create(Component, {
		initialize: function($super, opts) {
			// provide some default options, but let opts override them
			var options = Object.extend({
				mouseMoveEvents: true,
				mouseButtonEvents: true,
				text: "",
				normalColor: Color.white,
				hoverColor: Color.lightGray,
				pressColor: Color.gray,
				font: new gamejs.font.Font("14px Sans-serif")
			}, opts);
			
			this.listeners = [];
			
			// pass all the options to Component (it'll call set() below)
			$super(options);
		},
		
		set: function($super, props) {
			$super(props);
			
			// check for a 'text' property. if so, set it and force a repaint
			if (props.text) {
				this.text = props.text;
				this.invalidate();
			}
			
			// check for a new 'font' property, and force a repaint if needed
			if (props.font) {
				this.font = props.font;
				this.invalidate();
			}
			
			if (props.normalColor) {
				this.normalColor = props.normalColor;
				this.invalidate();
			}
			
			if (props.hoverColor) {
				this.hoverColor = props.hoverColor;
				this.invalidate();
			}
			
			if (props.pressColor) {
				this.pressColor = props.pressColor;
				this.invalidate();
			}
			
			// if a listener is provided (e.g., in the constructor), add it
			if (props.listener) {
				this.addListener(props.listener);
			}
		},
		
		onMouseEntered: function(event) {
			this.backgroundColor = this.hoverColor;
			this.invalidate();
		},
		
		onMouseExited: function(event) {
			this.backgroundColor = this.normalColor;
			this.invalidate();
		},
		
		onMousePress: function(event) {
			this.backgroundColor = this.pressColor;
			this.invalidate();
		},
		
		onMouseRelease: function(event) {
			this.backgroundColor = this.hoverColor;
			this.invalidate();
			
			this.listeners.forEach(function(listener) {
				listener(this, event);
			}, this);
		},
		
		/**
		 * Adds the given listener to this button, to be notified of button
		 * click events.
		 * @param {function} listener
		 */
		addListener: function(listener) {
			this.listeners.push(listener);
		},
		
		/**
		 * Removes the given listener from this button
		 * @param {function} listener
		 */
		removeListener: function(listener) {
			this.listeners.remove(listener);
		},
		
		paintComponent: function(surface) {
			surface.clear();
			
			// make sure we have text and a font to use
			if (this.text && this.font) {
				// init the default background if necessary
				if (!this.backgroundColor) {
					this.backgroundColor = this.normalColor;
				}
				
				// draw the background
				gamejs.draw.rect(
						surface,
						this.backgroundColor.string(),
						new gamejs.Rect(0, 0, this.width(), this.height()));
				
				
				// render the text onto a Surface
				var fontSurface = this.font.render(this.text);
				var size = fontSurface.getSize();
				
				// find the centered x,y for the text inside this Component
				var center = [
					(this.width() / 2) - (size[0] / 2),
					(this.height() / 2) - (size[1] / 2)
				];
				
				// copy the text to the Component's surface
				surface.blit(fontSurface, center);
				
				// draw a 1px wide crappy border
				gamejs.draw.rect(surface, "#000",
						new gamejs.Rect(1, 1, this.width() - 2, this.height() - 2),
						1);
			}
		}
	});
});
