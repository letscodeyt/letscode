define(function(require) {
	require("prototype");
	var gamejs = require("gamejs");
	
	/**
	* @fileOverview Defines a basic GUI component
	*/
	
	return Class.create({
		
		/**
		 * Initializes a new Component. The passed options object may be used
		 * to override certain component parameters, such as the component
		 * bounds.
		 * @param {Object} opts options to override the class defaults.
		 */
		initialize: function(opts) {
			var options = Object.extend({
				bounds: new gamejs.Rect(0, 0, 100, 100),
				continuous: false,
				focusable: false,
				mouseMoveEvents: false,
				mouseButtonEvents: false
			}, opts);
			
			this.set(options);
			
			this.invalid = true;
			this.focused = false;
			this.mouseInside = false;
		},
		
		/**
		 * Sets an arbitrary set of properties as defined by the provided
		 * properties object, and additionally handles event processing for
		 * various events such as resizing.
		 * 
		 * Subclasses should override this method to add support for custom
		 * properties, but will also need to have a call to $super.set() to
		 * ensure higher-level properties are still processed.
		 * 
		 * @param {Object} props
		 * @returns this
		 */
		set: function(props) {
			var sizeChanged = false;
			
			if (props.bounds) {
				this.bounds = props.bounds;
				sizeChanged = true;
			}
			
			if (props.position) {
				this.bounds.x = props.position.x;
				this.bounds.y = props.position.y;
			}
			
			if (props.size) {
				this.bounds.width = props.size.width;
				this.bounds.height = props.size.height;
				sizeChanged = true;
			}
			
			// we need typeof in order to accept 0 as a possible value
			if (typeof props.x !== 'undefined') {
				this.bounds.left = props.x;
			}
			
			if (typeof props.y !== 'undefined') {
				this.bounds.top = props.y;
			}
			
			if (props.width) {
				this.bounds.width = props.width;
				sizeChanged = true;
			}
			
			if (props.height) {
				this.bounds.height = props.height;
				sizeChanged = true;
			}
			
			if (typeof props.invalid !== 'undefined') {
				this.invalid = props.invalid;
			}
			
			if (typeof props.continuous !== 'undefined') {
				this.continuous = props.continuous;
			}
			
			if (typeof props.focusable !== 'undefined') {
				this.focusable = props.focusable;
			}
			
			if (typeof props.focused !== 'undefined') {
				this.focused = props.focused;
				console.log("component focused");
			}
			
			if (typeof props.mouseMoveEvents !== 'undefined') {
				this.mouseMoveEvents = props.mouseMoveEvents;
			}
			
			if (typeof props.mouseButtonEvents !== 'undefined') {
				this.mouseButtonEvents = props.mouseButtonEvents;
			}
			
			if (typeof props.parent !== "undefined") {
				this.parent = props.parent;
			}
			
			if (sizeChanged) {
				this.resize();
			}
			
			return this;
		},
		
		/**
		 * Gets the x position of this component, as defined by its bounds as
		 * the left edge of the bounding rectangle.
		 * @param {Number} newX Optional new x position
		 * @returns {Number} the current x coordinate
		 */
		x: function(newX) {
			if (typeof newX !== 'undefined') {
				this.set({ x: newX });
			}
			
			return this.bounds.left;
		},
	
		/**
		 * Gets the y position of this component, as defined by its bounds as
		 * the top edge of the bounding rectangle.
		 * @param {Number} newY Optional new y position
		 * @returns {Number} the current y coordinate
		 */
		y: function(newY) {
			if (typeof newY !== 'undefined') {
				this.set({ y: newY });
			}
			
			return this.bounds.top;
		},
		
		/**
		 * Gets the width of this component as defined by its bounds. If a
		 * value is provided, the width will be changed.
		 * @param {Number} w Optional new width to set
		 * @returns {Number} the object width
		 */
		width: function(w) {
			if (w) {
				this.set({ width: w });
			}
			
			return this.bounds.width;
		},
		
		/**
		 * Gets the height of this component as defined by its bounds. If a
		 * value is provided, the height will be changed.
		 * @param {Number} h Optional new height to set
		 * @returns {Number} the object height
		 */
		height: function(h) {
			if (h) {
				this.set({ height: h });
			}
			
			return this.bounds.height;
		},
		
		/**
		 * Handles the resize event for this component. This primarily includes
		 * recreating a surface of the appropriate size, and invalidating the
		 * component to force a redraw.
		 * @returns this
		 */
		resize: function() {
			this.surface = new gamejs.Surface(this.bounds);
			this.invalid = true;
			
			return this;
		},
		
		/**
		 * Invalidates the component, flagging it to be completely repainted
		 * during the next paint cycle.
		 * @returns this
		 */
		invalidate: function() {
			this.invalid = true;
		},
		
		/**
		 * Draws this component on the given surface. Components are only
		 * completely redrawn if invalid or if this component is flagged as
		 * continuous.
		 * @param {gamejs.Surface} display the display surface to draw on
		 * @returns this
		 */
		draw: function(display) {
			if (!this.surface) {
				this.resize(); // attempt to reinitialize it properly
			}
			
			if (this.invalid || this.continuous) {
				this.paintComponent(this.surface); // redraw as needed
				this.invalid = false;
			}
			
			display.blit(this.surface, [ this.x(), this.y() ]);
		},
		
		/**
		 * Draws this component onto the given surface. Note that the surface
		 * provided is a local surface for the component itself. Because of
		 * this, drawing operations should use local coordinates rather than
		 * absolute coordinates.
		 * 
		 * Note that this method will only be called during the draw cycle if
		 * the component has been invalidated or the continuous flag is set.
		 * @param {gamejs.Surface} surface The Surface to draw onto
		 * @returns this
		 */
		paintComponent: function(surface) {
			// to be implemented by subclasses
			return this;
		},
		
		/**
		 * Processes input events. This function should not be called by
		 * subclasses, but may be overridden to handle gamejs events that aren't
		 * dispatched here.
		 * @param {Array} events
		 * @returns {this}
		 */
		processEvents: function(events) {
			events.forEach(function(event) {
				// localize the position and save it back if applicable
				if (event.pos) {
					event.localPos = this.normalizePoint(event.pos);
				}
				
				// check the event type and handle it as needed
				// this dispatches different event types to dedicated class
				// functions that subclasses can implement, like onMousePress()
				switch (event.type) {
					case gamejs.event.MOUSE_DOWN:
						if (this.mouseButtonEvents) {
							if (this.bounds.collidePoint(event.pos)) {
								this.onMousePress(event);
								
								if (this.focusable) {
									this.parent.setFocus(this);
								}
							}
						}
						break;
						
					case gamejs.event.MOUSE_UP:
						if (this.mouseButtonEvents) {
							if (this.bounds.collidePoint(event.pos)) {
								this.onMouseRelease(event);
							}
						}
						break;
						
					case gamejs.event.MOUSE_MOTION:
						if (this.mouseMoveEvents) {
							if (this.bounds.collidePoint(event.pos)) {
								this.onMouseMove(event);

								if (!this.mouseInside) {
									// now it is
									this.mouseInside = true;
									this.onMouseEntered(event);
								}
							} else {
								if (this.mouseInside) {
									// not anymore!
									this.mouseInside = false;
									this.onMouseExited(event);
								}
							}
						}
						
						break;
						
					case gamejs.event.KEY_DOWN:
						if (this.focused) {
							this.onKeyPress(event);
						}
						break;
						
					case gamejs.event.KEY_UP:
						if (this.focused) {
							this.onKeyRelease(event);
						}
						break;
				}
			}, this);
			
			return this;
		},
		
		/**
		 * Normalizes the given point to use component-local coordinates.
		 * @param {Array} point the x,y array defining the point
		 * @returns {Array} the point in component-local coordinates
		 */
		normalizePoint: function(point) {
			return [ point[0] - this.x(), point[1] - this.y() ];
		},
		
		onMousePress: function(event) {
	
		},
		
		onMouseRelease: function(event) {
			
		},
		
		onMouseMove: function(event) {
	
		},
		
		onMouseEntered: function(event) {
	
		},
		
		onMouseExited: function(event) {
	
		},
		
		onKeyPress: function(event) {
			
		},
		
		onKeyRelease: function(event) {
	
		}
	});
});

