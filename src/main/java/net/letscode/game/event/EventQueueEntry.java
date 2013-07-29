package net.letscode.game.event;

import java.lang.reflect.Method;
import java.util.Objects;
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
	
	@Getter
	private boolean vetoable;

	public EventQueueEntry(
			Object object, Method method, int priority, boolean vetoable) {
		this.object = object;
		this.method = method;
		this.priority = priority;
		this.vetoable = vetoable;
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

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 79 * hash + Objects.hashCode(this.object);
		hash = 79 * hash + Objects.hashCode(this.method);
		hash = 79 * hash + this.priority;
		hash = 79 * hash + (this.vetoable ? 1 : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final EventQueueEntry other = (EventQueueEntry) obj;
		if (!Objects.equals(this.object, other.object)) {
			return false;
		}
		if (!Objects.equals(this.method, other.method)) {
			return false;
		}
		if (this.priority != other.priority) {
			return false;
		}
		if (this.vetoable != other.vetoable) {
			return false;
		}
		return true;
	}

	
}
