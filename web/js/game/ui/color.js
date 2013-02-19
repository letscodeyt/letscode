define(function(require) {
	require("prototype");
	
	/**
	 * @fileOverview Defines a Color class, and allows for some basic
	 * manipulation. Inspired partially by AWT's Color class, and roughly ported
	 * from Apache Harmony's java.awt.Color implementation.
	 */
	
	// create the new Color class
	var Color = Class.create({
		
		/**
		 * Initializes a new Color with the values specified in opts. The
		 * parameters may be either a masked integer RGBA as `opts.value`, or
		 * the parameters can be specified as `opts.red`, `opts.green`,
		 * `opts.blue`, and `opts.alpha`. Shorthand names `r`, `g`, `b`, and `a`
		 * may be used as well. The separate options may either be ints 0 - 255,
		 * or floats 0.0 - 1.0, which will be internally converted to 0 - 255
		 * ints.
		 * <p>Alternatively, colors may be specified by a `name` parameter.
		 * If a name parameter exists, css() return it directly, but operations
		 * that require r/g/b/a values will fail.</p>
		 * @param {Object} opts an object containing color options
		 * @returns {this}
		 */
		initialize: function(opts) {
			if (opts.name) {
				// special case: there's no good way to convert from css name
				this.name = name;
			} else if (opts.value) {
				if (opts.hasAlpha) {
					this.value = opts.value;
				} else {
					this.value = opts.value | 0xFF000000;
				}
			} else {
				// use short circuit operators to allow 'red' or 'r'
				var r = opts.red   || opts.r;
				var g = opts.green || opts.g;
				var b = opts.blue  || opts.b;
				
				// convert floats to ints as needed
				r = this.isFloat(r) ? this.floatToInt(r) : r;
				g = this.isFloat(g) ? this.floatToInt(g) : g;
				b = this.isFloat(b) ? this.floatToInt(b) : b;
				
				if (opts.alpha || opts.a) {
					var a = opts.alpha || opts.a;
					a = this.isFloat(r) ? this.floatToInt(r) : r;
					
					// do some bitshifting to merge the r/g/b/a values
					// each of the 4 is an 8 bit integer (0-255), and JS ints
					// are 32 bits, so this fits nicely
					this.value = b | (g << 8) | (r << 16) | (a << 24);
				} else {
					// same as above, but give some a defaulta alpha of 255
					this.value = b | (g << 8) | (r << 16) | 0xFF000000;
				}
			}
			
			// multiplier for darker() and brighter()
			this.scaleFactor = 0.7;
			
			// the minimum amount brighter() and darker() should change after
			// the multiplier
			this.minScalable = 3;
		},
		
		/**
		 * Checks if the provided number is floating point.
		 * @param {type} n the number to check
		 * @returns {boolean} true if n is a non-integer number
		 */
		isFloat: function(n) {
			return (typeof n === 'number') && (n % 1 !== 0);
		},
		
		/**
		 * Converts ("scales") a float 0.0 - 1.0 to an integer 0 - 255.
		 * @param {type} float the floating point value to convert
		 * @returns {number} the converted value
		 */
		floatToInt: function(float) {
			return Math.floor((float * 255) + 0.5);
		},
		
		/**
		 * Gets the RGB value of this color, as a masked integer containing
		 * 8 bits each of red/green/blue/alpha integer values.
		 * <p>If this is a named color (direct CSS name / string hex value),
		 * an error will be thrown.</p>
		 * @returns {number}
		 */
		rgb: function() {
			if (this.name) {
				throw "Can't get rgb of a named color";
			}
			
			return this.value;
		},
		
		/**
		 * Gets the red value of this color, as an integer between 0 and 255.
		 * <p>If this is a named color (direct CSS name / string hex value),
		 * an error will be thrown.</p>
		 * @returns {number} an integer 0 - 255
		 */
		red: function() {
			if (this.name) {
				throw "Can't get rgb of a named color";
			}
			
			return (this.value >> 16) & 0xFF;
		},
		
		/**
		 * Gets the green value of this color, as an integer between 0 and 255.
		 * <p>If this is a named color (direct CSS name / string hex value),
		 * an error will be thrown.</p>
		 * @returns {number} an integer 0 - 255
		 */
		green: function() {
			if (this.name) {
				throw "Can't get rgb of a named color";
			}
			
			return (this.value >> 8) & 0xFF;
		},
		
		/**
		 * Gets the blue value of this color, as an integer between 0 and 255.
		 * <p>If this is a named color (direct CSS name / string hex value),
		 * an error will be thrown.</p>
		 * @returns {number} an integer 0 - 255
		 */
		blue: function() {
			if (this.name) {
				throw "Can't get rgb of a named color";
			}
			
			return this.value & 0xFF;
		},
		
		/**
		 * Gets the alpha value of this color, as an integer between 0 and 255.
		 * <p>If this is a named color (direct CSS name / string hex value),
		 * an error will be thrown.</p>
		 * @returns {number} an integer 0 - 255
		 */
		alpha: function() {
			if (this.name) {
				throw "Can't get rgb of a named color";
			}
			
			return (this.value >> 24) & 0xFF;
		},
		
		/**
		 * Returns a new Color instance that is darker than the current value
		 * by the defined scale factor. The current alpha value will be
		 * preserved.
		 * <p>If this is a named color (direct CSS name / string hex value),
		 * an error will be thrown.</p>
		 * @returns {Color} a new darker color
		 */
		darker: function() {
			if (this.name) {
				throw "Named colors can not be darkened.";
			}
			
			return new Color({
				r: Math.floor(this.red() * this.scaleFactor),
				g: Math.floor(this.green() * this.scaleFactor),
				b: Math.floor(this.blue() * this.scaleFactor),
				a: this.alpha()
			});
		},
		
		/**
		 * Returns a new instance of a Color brightened by the defined scale
		 * factor. If any of this color's values are zero, they will be set to
		 * the defined minScalable value. Alpha values will be preserved.
		 * <p>If this is a named color (direct CSS name / string hex value),
		 * an error will be thrown.</p>
		 * @returns {Color} a new, brighter Color instance
		 */
		brighter: function() {
			if (this.name) {
				throw "Named colors can not be brightened.";
			}
			
			var r = this.red();
			var g = this.green();
			var b = this.blue();
			
			// do a zero check - division with 0 / scaleFactor won't increase
			if (r === 0) {
				r = this.minScalable;
			} else {
				// set the new r value to 1/scaleFactor (~10/7ths) the current,
				// or 255 - whichever is the smallest.
				r = Math.min(Math.floor(r / this.scaleFactor), 255);
			}
			
			if (g === 0) {
				g = this.minScalable;
			} else {
				g = Math.min(Math.floor(g / this.scaleFactor), 255);
			}
			
			if (b === 0) {
				b = this.minScalable;
			} else {
				b = Math.min(Math.floor(b / this.scaleFactor), 255);
			}
			
			// return a new Color with the brighter values.
			return new Color({
				red:   r,
				green: g,
				blue:  b,
				alpha: a
			});
		},
		
		/**
		 * Returns a hex color string with the rgb values of this color. Note
		 * that this does not include the alpha value for the color, if any.
		 * @returns {String} the hex color string
		 */
		hex: function() {
			return "#"
					+ this.red().toString(16)
					+ this.green().toString(16)
					+ this.blue().toString(16);
		},
		
		/**
		 * Creates a css color string representing this color, in the form of
		 * "rgb(r, g, b)".
		 * @returns {String} A valid CSS color string
		 */
		css: function() {
			// return the direct color name
			if (this.name) {
				return this.name;
			}
			
			return "rgb("
					+ this.red() + ","
					+ this.green() + ","
					+ this.blue() + ")";
		},
		
		/**
		 * Creates a css color string representing this color, in the form of
		 * "rgba(r, g, b, a)". Note: this may not be supported by all browsers,
		 * but probably alright in IE9, Firefox, WebKit, and Opera 10+.
		 * @returns {String} A valid CSS color string
		 */
		cssAlpha: function() {
			if (this.name) {
				return this.name;
			}
			
			return "rgba("
					+ this.red() + ","
					+ this.green() + ","
					+ this.blue() + ","
					+ this.alpha() + ")";
		},
		
		/**
		 * Returns a string compatible with gamejs drawing functions. Presently
		 * this returns this.cssAlpha(), and includes the alpha byte.
		 * @returns {string}
		 */
		string: function() {
			if (this.name) {
				return this.name;
			}
			
			return this.cssAlpha();
		}
		
	});
	
	// define a few default colors
	// note that due to a limitation in Prototype, these values won't be copied
	// to subclasses. so, a MyColor subclass won't have a `green` property, etc
	Color.white     = new Color({r: 255, g: 255, b: 255});
	Color.lightGray = new Color({r: 192, g: 192, b: 192});
	Color.gray      = new Color({r: 128, g: 128, b: 128});
	Color.darkGray  = new Color({r: 64,  g: 64,  b: 64});
	Color.black     = new Color({r: 0,   g: 0,   b: 0});
	Color.red       = new Color({r: 255, g: 0,   b: 0});
	Color.pink      = new Color({r: 255, g: 175, b: 175});
	Color.orange    = new Color({r: 255, g: 200, b: 0});
	Color.yellow    = new Color({r: 255, g: 200, b: 0});
	Color.green     = new Color({r: 0,   g: 255, b: 0});
	Color.magenta   = new Color({r: 255, g: 0,   b: 255});
	Color.cyan      = new Color({r: 0,   g: 255, b: 255});
	Color.blue      = new Color({r: 0,   g: 0,   b: 255});
	
	return Color;
});
