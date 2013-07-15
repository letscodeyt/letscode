package net.letscode.game.event;

/**
 * Defines basic event levels. Registered events with higher priority levels
 * will be ordered first in the event queue for each event type.
 * @author timothyb89
 */
public class EventPriority {
	
	public static final int HIGHEST = 3;
	public static final int HIGHER = 2;
	public static final int HIGH = 1;
	public static final int NORMAL = 0;
	public static final int LOW = -1;
	public static final int LOWER = -2;
	public static final int LOWEST = -3;
	
}
