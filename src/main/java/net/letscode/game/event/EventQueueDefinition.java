package net.letscode.game.event;

import java.util.PriorityQueue;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author tim
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
		for (EventQueueEntry e : queue) {
			log.debug("Notifying listener: ", e.getMethod());
			
			try {
				e.notify(event);
			} catch (EventVetoException ex) {
				// skip on event veto
				break;
			}
		}
	}
	
}
