package net.letscode.game.event;

import java.util.PriorityQueue;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author timothyb89
 */
@Slf4j
public class EventQueueDefinition {
	
	@Getter
	private Class<? extends Event> eventType;
	
	@Getter
	private PriorityQueue<EventQueueEntry> queue;
	
	public EventQueueDefinition(Class<? extends Event> eventType) {
		this.eventType = eventType;
		
		queue = new PriorityQueue();
	}
	
	public void push(Event event) {
		boolean vetoed = false;
		
		for (EventQueueEntry e : queue) {
			log.debug("Notifying listener: ", e.getMethod());
			
			// if the event has been vetoed, and this event is vetoable,
			// skip it
			if (vetoed && e.isVetoable()) {
				continue;
			}
			
			try {
				e.notify(event);
			} catch (EventVetoException ex) {
				// skip others on event veto
				vetoed = true;
			}
		}
	}
	
}
