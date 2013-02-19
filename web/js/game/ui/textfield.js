define(function(require) {
	require("prototype");
	var gamejs = require("gamejs");
	var Component = require("game/ui/component");
	var Color = require("game/ui/color");
	
	return Class.create(Component, {
		initialize: function($super, opts) {
			var options = Object.extend({
				focusable: true,
				mouseButtonEvents: true,
				backgroundColor: Color.white,
				text: "",
				font: new gamejs.font.Font("14px sans-serif"),
				fontColor: Color.black
			}, opts);
			
			$super(options);
		},
		
		set: function($super, props) {
			$super(props);
			
			if (typeof props.text !== "undefined") { // allow empty string
				this.text = props.text;
				this.invalidate();
			}
			
			if (props.backgroundColor) {
				this.backgroundColor = props.backgroundColor;
				this.invalidate();
			}
			
			if (props.font) {
				this.font = props.font;
				this.invalidate();
			}
			
			if (props.fontColor) {
				this.fontColor = props.fontColor;
				this.invalidate();
			}
		},
		
		onKeyPress: function(event) {
			console.log(event);
			console.log(String.fromCharCode(event.key));
		},
		
		paintComponent: function(surface) {
			surface.clear();
			
			gamejs.draw.rect(surface,
					"#000",
					new gamejs.Rect(1, 1, this.width() - 2, this.height() - 2),
					1);
		}
	});
});
