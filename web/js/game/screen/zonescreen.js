define(function(require) {
	require("prototype");
	var gamejs = require("gamejs");
	var GameScreen = require("game/ui/gamescreen");
	var Button = require("game/ui/button");
	var Aligner = require("game/ui/aligner");
	var DialogForm = require("game/ui/jq/dialogform");

	var $ = require("jquery");
	require("jquery-ui");

	return Class.create(GameScreen, {
		initialize: function($super) {
			$super();

			var buttons = [];
			var aligner = new Aligner({
				bounds: new gamejs.Rect(580, 20, 200, 440),
				components: buttons
			});

			var testButton = new Button({
				text: "Send test message",
				height: 50,
				listener: function() {
					var d = new DialogForm({
						title: "Input",
						fields: {
							"Message": {
								name: "text",
								type: "text"
							}
						},
						buttons: [{
								text: "Send",
								click: function() {
									// we have to require() game after the fact
									// to prevent circular dependency problems
									var game = require("game/game").getInstance();
									game.connection.send({
										type: "chat",
										text: d.get("text")
									});
								}
							}]
					});
					
					
				}
			});
			buttons.push(testButton);
			this.addComponent(testButton);

			aligner.align();
		}

	});
});


