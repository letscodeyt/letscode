define(function(require) {
	require("prototype");
	var gamejs = require("gamejs");
	var GameScreen = require("game/ui/gamescreen");
	var Color = require("game/ui/color");
	
	return Class.create(GameScreen, {
		initialize: function($super) {
			$super();
			
			this.font = new gamejs.font.Font("36px Arial");
			this.subFont = new gamejs.font.Font("16px Courier sans-serif");
			
			this.game = window.game;
			
			this.text = "connecting";
			this.dots = "";
			this.lastDot = new Date().getTime();
		},
		
		draw: function(surface) {
			surface.fill(Color.gray.string());
			
			var surfaceSize = surface.getSize();
			
			var white = Color.white.string();
			
			var time = new Date().getTime();
			if (time - this.lastDot > 250) {
				if (this.dots.length === 3) {
					this.dots = "";
				} else {
					this.dots += ".";
				}
				
				this.lastDot = time;
			}
			
			var mainText = this.font.render(this.text + this.dots, white);
			var mainSize = mainText.getSize();
			var mainX = (surfaceSize[0] / 2) - (mainSize[0] / 2);
			
			var statusText = this.subFont.render(this.game.connection.status, white);
			var statusSize = statusText.getSize();
			var statusX = (surfaceSize[0] / 2) - (statusSize[0] / 2);
			
			var mainY = (surfaceSize[1] / 2) 
			            - (mainSize[1] + statusSize[1] + 5) / 2;
			var statusY = mainY + mainSize[1] + 5;
			
			surface.blit(mainText, [mainX, mainY]);
			surface.blit(statusText, [statusX, statusY]);
			
			this.drawComponents(surface);
		}
	});
	
});
