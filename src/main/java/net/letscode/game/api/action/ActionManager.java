package net.letscode.game.api.action;

import lombok.extern.slf4j.Slf4j;

/**
 * Manages the 
 * @author timothyb89
 */
@Slf4j
public class ActionManager {
	
	private static ActionManager instance;
	
	private ActionManager() {
		
	}
	
	public static ActionManager get() {
		if (instance == null) {
			instance = new ActionManager();
		}
		
		return instance;
	}
	
}
