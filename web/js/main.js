/*
 * main.js
 * This file mainly handles library initialization, including the configuration
 * of requirejs for the prototype and gamejs shims, and then initializes the
 * Game class.
 */

requirejs.config({
	baseUrl: "js/",
	packages: [
		{
			name: "gamejs",
			main: "gamejs"
		}
	],
	paths: {
		"prototype": "lib/prototype",
		"jquery-ui": "lib/jquery-ui"
	},
	shim: {
		"prototype": {
			exports: "Prototype" // close enough...
		},
		"jquery-ui": [ "jquery" ]
	}
});

// Array Remove - By John Resig (MIT Licensed)
Array.prototype.remove = function(from, to) {
	var rest = this.slice((to || from) + 1 || this.length);
	this.length = from < 0 ? this.length + from : from;
	return this.push.apply(this, rest);
};

/**
 * Initializes the Game class. Automatically waits for the dom to finish
 * loading via the domReady plugin and requirejs's Loader API.
 * @param {type} doc the loaded document (unused)
 * @param {type} gamejs the loaded gamejs instance
 * @param Game the loaded Game class
 */
require(["domReady!", "gamejs", "game/game"], function(doc, gamejs, game) {
	var game = game.getInstance();

	gamejs.ready(function() {
		gamejs.time.interval(game.gameTick, game);
	});
});
