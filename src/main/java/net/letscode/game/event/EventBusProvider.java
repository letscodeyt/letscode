package net.letscode.game.event;

/**
 * A simple denotation that a particular class has an event bus
 * @author timothyb89
 */
public interface EventBusProvider {
	
	/**
	 * Gets the EventBusClient for this ClientSession, which allows other
	 * classes to register themselves to receive event notifications.
	 * @return the {@link EventBusClient} for this class
	 */
	public EventBusClient bus();
	
}
