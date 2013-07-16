package net.letscode.game.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Defines an event bus that handles the dispatching of events to client
 * classes.
 * @author timothyb89
 */
@Slf4j
public class EventBus {
	
	private List<EventQueueDefinition> definitions;
	
	/**
	 * Client-safe interface for the event bus
	 */
	@Getter
	private EventBusClient client;
	
	public EventBus() {
		definitions = new ArrayList<>();
		
		client = new EventBusClient(this);
	}
	
	/**
	 * Defines a new event type. A new event queue will be added for the
	 * provided class, and future invocations of {@link push(Event)} will notify
	 * registered listeners.
	 * @param e the event class to register
	 */
	public void add(Class<? extends Event> clazz) {
		definitions.add(new EventQueueDefinition(clazz));
	}
	
	/**
	 * Gets the EventQueueDefinition for the given class. If no event queue has
	 * been created for the given class, {@code null} is returned. Note that
	 * this will not return superclasses of the given class, only exact matches.
	 * @param clazz The class to search for
	 * @return the event queue for the given class
	 */
	public EventQueueDefinition getQueueForClass(Class<? extends Event> clazz) {
		for (EventQueueDefinition d : definitions) {
			if (d.getEventType() == clazz) {
				return d;
			}
		}
		
		return null;
	}
	
	/**
	 * Removes the event queue for the given class. If no queue for the given
	 * class is found, the method fails silently.
	 * @param clazz the class for which to remove the queue
	 */
	public void remove(Class<? extends Event> clazz) {
		definitions.remove(getQueueForClass(clazz));
	}
	
	/**
	 * Pushes the given event to the message bus. This will notify listeners in
	 * order of their priority; any handler that throws an
	 * {@link EventVetoException} will cause handlers further down in the queue
	 * to be skipped. Specifically, this notifies the event queue that exactly
	 * matches the class (as handlers are added to superclass queues at
	 * registration time)
	 * <p>If no queue exists for the given event type, no listeners will be
	 * notified and the method will fail silently.</p>
	 * @param event the event to push
	 */
	public void push(Event event) {
		getQueueForClass(event.getClass()).push(event);
	}
	
	/**
	 * Registers the given method to the event bus. The object is assumed to be
	 * of the class that contains the given method.
	 * <p>The method parameters are
	 * checked and added to the event queue corresponding to the {@link Event}
	 * used as the first method parameter. In other words, if passed a reference
	 * to the following method:</p>
	 * <p><code>
	 * public void xyzHandler(XYZEvent event) { ... }
	 * </code></p>
	 * <p>... <code>xyzHandler</code> will be added to the event queue for
	 * {@code XYZEvent} assuming that {@code XYZEvent} extends {@link Event} and
	 * an event queue has been created for that event type.</p>
	 * @param o an instance of the class containing the method <code>m</code>
	 * @param m the method to register
	 */
	protected void registerMethod(Object o, Method m, int priority) {
		// check the parameter types, and attempt to resolve the event
		// type
		if (m.getParameterTypes().length != 1) {
			log.warn("Skipping invalid event handler definition: " + m);
			return;
		}

		// make sure the parameter is an Event
		// this additional/technically unneeded check should cut down on the
		// time required to process the loop over the definitions for methods
		// that don't match
		Class<?> param = m.getParameterTypes()[0];
		if (!Event.class.isAssignableFrom(param)) {
			log.warn("Skipping event handler without an Event parameter: " + m);
			return;
		}
	
		// add the method to all assignable definitions.
		// this may result in the method being added to multiple queues,
		// that is, the queues for each superclass.
		for (EventQueueDefinition d : definitions) {
			if (param.isAssignableFrom(d.getEventType())) {
				d.getQueue().add(new EventQueueEntry(o, m, priority));
				log.debug("Added {} to queue {}", m, d.getEventType());
			}
		}
	}
	
	/**
	 * Registers all methods of the given object annotated with
	 * {@link EventHandler}.
	 * @see EventBus#registerMethod(Object, Method, int)
	 * @param o the object to process
	 */
	public void register(Object o) {
		for (Method m : o.getClass().getMethods()) {
			if (m.isAnnotationPresent(EventHandler.class)) {
				// get the priority from the annotation
				EventHandler h = m.getAnnotation(EventHandler.class);
				int priority = h.priority();
				
				registerMethod(o, m, priority);
			}
		}
	}
	
}
