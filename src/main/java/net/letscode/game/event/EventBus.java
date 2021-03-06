package net.letscode.game.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Defines an event bus that handles the dispatching of events to client
 * classes. The owner class can define "buckets" or queues for subclasses of
 * {@link Event}, which may then be joined by client classes. The owner can then
 * push an instance of the {@code Event} to the bus using {@link #push(Event)}
 * which will notify all registered classes listening for that event type, or
 * any of its superclasses.
 * <p>This implementation allows for inherited and overridden event types,
 * assuming that each superclass has defined a queue specifically for that type.
 * When listeners are registered, they are added to all queues 'compatible' with
 * their event type, meaning that they will be placed into the event queue for
 * their direct event type, and any assignable superclasses. Then, when an event
 * is pushed to the queue, only the queue for that exact type is notified. This
 * ensures that listeners will only ever receive a single notification for
 * events compatible via multiple superclasses.</p>
 * <p>Definition of listeners requires the {@link EventHandler} annotation. When
 * some client class calls {@link #register(Object)}, all methods with an
 * {@code @EventHandler} annotation are scanned and added to the appropriate
 * event queues.</p>
 * <p>{@code @EventHandler} takes an optional {@code priority} parameter. Events
 * with a higher priority (defined either as an integer or preferably using a
 * constant in {@link EventPriority}) will be notified before events with a
 * lower (more negative) priority.</p>
 * <p>Events may also be 'vetoed', in the sense that an event at a higher
 * priority may prevent handlers further down in the queue from being executed.
 * While useful, this should be used with caution to ensure potentially
 * important events may still get an opportunity to be notified.</p>
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
		EventQueueDefinition def = getQueueForClass(event.getClass());
		if (def != null) {
			def.push(event);
		}
	}
	
	/**
	 * Pushes the given event to the bus, but only notifies those with a
	 * priority flag greater than or equal to the given {@code priority}. Note
	 * that the default priority is {@link EventPriority#NORMAL} (0).
	 * @see #push(Event) 
	 * @see EventPriority
	 * @param event the event to push
	 * @param priority the minimum event priority 
	 */
	public void push(Event event, int priority) {
		EventQueueDefinition def = getQueueForClass(event.getClass());
		if (def != null) {
			def.push(event, priority);
		}
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
	 * @param priority the event priority
	 * @param vetoable vetoable flag
	 */
	protected void registerMethod(
			Object o, Method m, int priority, boolean vetoable) {
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
		// (this is intended and is fundamentally what makes subclassed events
		// work as expected)
		for (EventQueueDefinition d : definitions) {
			if (param.isAssignableFrom(d.getEventType())) {
				d.getQueue().add(new EventQueueEntry(o, m, priority, vetoable));
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
				boolean vetoable = h.vetoable();
				
				registerMethod(o, m, priority, vetoable);
			}
		}
	}
	
	/**
	 * Removes the given object from all event queues that it may be a member
	 * of. The object will immediately stop receiving events from this EventBus.
	 * @param o the object to remove
	 */
	public void deregister(Object o) {
		for (EventQueueDefinition def : definitions) {
			// a queue for the entries to remove
			// we can't remove them inline because we'd cause a
			// ConcurrentModificationException
			List<EventQueueEntry> removeQueue = new LinkedList<>();
			
			// find all entries to remove
			for (EventQueueEntry e : def.getQueue()) {
				if (e.getObject() == o) {
					removeQueue.add(e);
				}
			}
			
			// remove them
			def.getQueue().removeAll(removeQueue);
		}
	}
	
}
