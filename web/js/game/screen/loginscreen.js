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
			
			this.authListeners = [];
			
			var buttons = [];
			var aligner = new Aligner({
				bounds: new gamejs.Rect(580, 20, 200, 440),
				components: buttons
			});
			
			var $this = this;
			
			var loginButton = new Button({
				text: "Login",
				height: 50,
				listener: function() {
					var d = new DialogForm({
						title: "Login",
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
								text: "Login",
								click: function() {
									$this.notify({
										type: "login",
										username: d.get("username"),
										password: d.get("password")
									});
									d.close();
								}
							}, {
								text: "Cancel",
								click: function() {
									d.close();
								}
							}]
					});
				}
			});
			buttons.push(loginButton);
			this.addComponent(loginButton);
			
			var registerButton = new Button({
				text: "Register",
				height: 50,
				listener: function() {
					var d = new DialogForm({
						title: "Register",
						fields: {
							"Username": {
								name: "username",
								type: "text"
							},
							"Email Address": {
								name: "email",
								type: "email"
							},
							"Password": {
								name: "password",
								type: "password"
							},
							"Repeat": {
								name: "repeat",
								type: "password"
							}
						},
						buttons: [{
								text: "Login",
								click: function() {
									// do some quick password validation
									var p = d.get("password");
									var r = d.get("repeat");
									
									if (p !== r) {
										MessageDialog.error(
												"Passwords do not match.");
										return;
									}
									
									$this.notify({
										type: "register",
										username: d.get("username"),
										email: d.get("email"),
										password: d.get("password")
									});
									d.close();
								}
							}, {
								text: "Cancel",
								click: function() {
									d.close();
								}
							}]
					});
				}
			});
			buttons.push(registerButton);
			this.addComponent(registerButton);
			
			aligner.align();
		},
		
		draw: function($super, surface) {
			$super(surface);
		},
		
		authListener: function(l) {
			this.authListeners.push(l);
		},
		
		notify: function(data) {
			this.authListeners.forEach(function(l) {
				l(data);
			}, this);
		}

	});
});
