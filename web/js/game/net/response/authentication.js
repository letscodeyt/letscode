define(function(require) {
	require("prototype");
	
	var LoginScreen = require("game/screen/loginscreen");
	
	/**
	 * @fileOverview A response handler for authentication requests. This
	 * implementation causes the current game screen to transition to the 
	 * LoginScreen which can prompt the user for input. 
	 */
	
	var AuthenticationResponse = Class.create({
		initialize: function(request) {
			this.request = request;
			this.game = window.game;
			console.log("Game is ", this.game, "data: ", request);
			
			// init login screen
			var login = new LoginScreen();
			
			var $this = this;
			login.authListener(function(data) {
				$this.infoEntered(data);
			});
			
			this.game.gameScreen = login;
		},
		
		/**
		 * Called when login (or possibly registration) information has been
		 * entered by the user
		 * @param {Object} info
		 */
		infoEntered: function(info) {
			console.log(this.request);
			
			this.game.connection.send({
				type: "response",
				name: "login",
				id: this.request.id,
				data: info
			});
		}
	});
	
	return AuthenticationResponse;
});
