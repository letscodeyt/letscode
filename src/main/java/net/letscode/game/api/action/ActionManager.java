package net.letscode.game.api.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the 
 * @author timothyb89
 */
public class ActionManager {
	
	private static ActionManager instance;
	
	private Logger logger = LoggerFactory.getLogger(ActionManager.class);
	
	private ActionManager() {
		
	}
	
	public static ActionManager get() {
		if (instance == null) {
			instance = new ActionManager();
		}
		
		return instance;
	}
	
}
