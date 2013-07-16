package net.letscode.game.event;

/**
 * Provides client-level access to an {@link EventBus}. That is, this class
 * exposes only registration and deregistration functionality, allowing it to
 * be safely passed to client classes such that they will be unable to spawn new
 * events and add event queues directly.
 * @author timothyb89
 */
public class EventBusClient {
	
	private EventBus bus;

	public EventBusClient(EventBus bus) {
		this.bus = bus;
	}
	
	/**
	 * Registers all methods of the given object annotated with
	 * {@link EventHandler}.
	 * @see EventBus#registerMethod(Object, Method, int)
	 * @param o the object to process
	 */
	public void register(Object object) {
		bus.register(object);
	}
	
	public void deregister(Object object) {
		// TODO
		throw new UnsupportedOperationException("Not implemented yet.");
	}
	
}
