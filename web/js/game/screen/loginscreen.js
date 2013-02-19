define(function(require) {
	require("prototype");
	var gamejs = require("gamejs");
	var GameScreen = require("game/ui/gamescreen");
	var Button = require("game/ui/button");
	var Aligner = require("game/ui/aligner");
	var DialogForm = require("game/ui/jq/dialogform");
	var MessageDialog = require("game/ui/jq/messagedialog");
	
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
			
			var loginButton = new Button({
				text: "Login",
				height: 50,
				listener: function() {
					/*var d = new DialogForm({
						title: "Test Dialog",
						fields: {
							"Username": {
								name: "username",
								type: "text"
							},
							"Password": {
								name: "password",
								type: "password"
							}
						},
						buttons: [{
								text: "Test",
								click: function() {
									alert("You entered: " + d.get("test"));
								}
							}, {
								text: "Close",
								click: function() {
									d.close();
								}
							}]
					});*/
					MessageDialog.info("TODO login");
				}
			});
			buttons.push(loginButton);
			this.addComponent(loginButton);
			
			var registerButton = new Button({
				text: "Register",
				height: 50,
				listener: function() {
					MessageDialog.info("TODO register");
				}
			});
			buttons.push(registerButton);
			this.addComponent(registerButton);
			
			aligner.align();
		},
		
		draw: function($super, surface) {
			$super(surface);
		}

	});
});
