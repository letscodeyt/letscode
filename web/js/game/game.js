define(function(require) {
	require("prototype");
	var gamejs = require("gamejs");
	var Connection = require("./net/connection");
	var ConnectScreen = require("./screen/connectscreen");
	
	var Game = Class.create({
		initialize: function() {
			window.game = this; // ugly singleton
			
			this.bounds = new gamejs.Rect(0, 0, 800, 480);
			
			this.connection = new Connection();
			this.connection.connect();
			//this.gameScreen = new TitleScreen();
			this.gameScreen = new ConnectScreen();
			
			this.surface = gamejs.display.setMode([
				this.bounds.width,
				this.bounds.height
			]);
			
			this.frameCount = 0;
			this.updateTime = new Date().getTime();
			this.updateFrames = 0;
			this.startTime = new Date().getTime();
			
			this.debugFont = new gamejs.font.Font("10px Courier sans-serif");
		},
		
		gameTick: function(ms) {
			if (this.gameScreen) {
				this.gameScreen.draw(this.surface);
			}
			
			this.frameCount++;
			this.updateFrames++;
			
			var now = new Date().getTime();
			var totalTime = now - this.startTime;
			
			var updateDiff = now - this.updateTime;
			
			var average = 1000 * (this.frameCount / totalTime);
			var current = 1000 * (this.updateFrames / updateDiff);
			
			this.surface.blit(this.debugFont.render(
					"FPS Avg: " + average.toFixed(1) + ", Curr: " + current.toFixed(1)),
					[5, 465]);
			
			var connStatus = this.debugFont.render(
					"connection status: " + this.connection.status);
			var x = this.bounds.width - connStatus.getSize()[0] - 5;
			this.surface.blit(connStatus, [x, 465]);
			
			// reset every second or so
			if (now - this.updateTime > 1000) {
				this.updateTime = now;
				this.updateFrames = 0;
			}
		}
	});
	
	return Game;
});
