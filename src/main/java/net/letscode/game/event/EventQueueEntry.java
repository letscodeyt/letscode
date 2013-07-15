package net.letscode.game.event;

import java.lang.reflect.Method;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Defines an entry in the event queue for a specific event type
 * @author timothyb89
 */
@Slf4j
public class EventQueueEntry implements Comparable<EventQueueEntry> {
	
	@Getter
	public Object object;
	
	@Getter
	public Method method;
	
	@Getter
	private int priority;

	public EventQueueEntry(Object object, Method method, int priority) {
		this.object = object;
		this.method = method;
		this.priority = priority;
	}
	
	@Override
	public int compareTo(EventQueueEntry o) {
		return o.priority - priority;
	}
	
	/**
	 * Notifies this queue entry of an event. Note that this blatantly assumes
	 * that the passed event is compatible with the method associated with this
	 * entry (as it was checked at registration time). As such, any outside 
	 * invocations of this method will need to manually check this.
	 * @param event the event to pass to this queue entry
	 */
	public void notify(Event event) {
		try {
			method.invoke(object, event);
		} catch (EventVetoException ex) {
			// skip this - it needs to be passed to the queue to skip properly
			throw ex;
		} catch (Exception ex) {
			// we don't want non-veto exceptions to break the entire event queue
			// so we catch and log the error here
			log.error("Error in event handler " + method, ex);
		}
	}
	
}
