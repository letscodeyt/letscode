package net.letscode.game.event.demo;

import lombok.Getter;
import lombok.ToString;
import net.letscode.game.event.Event;
import net.letscode.game.event.EventBus;

/**
 * A sample implementation of an object that uses an {@link EventBus} to provide
 * notification functionality.
 * @author timothyb89
 */
public class DemoEmitter {
	
	@Getter
	private EventBus bus;
	
	public DemoEmitter() {
		bus = new EventBus() {{
			add(DemoEventA.class);
			add(DemoEventAB.class);
			add(DemoEventB.class);
		}};
	}
	
	public void fireA(String message) {
		bus.push(new DemoEventA(message));
	}
	
	public void fireAB(String message, String person) {
		bus.push(new DemoEventAB(message, person));
	}
	
	public void fireB(int number) {
		bus.push(new DemoEventB(number));
	}

	@ToString
	public class DemoEventA extends Event {
		
		@Getter
		private String message;
		
		public DemoEventA(String message) {
			this.message = message;
		}
		
	};
	
	@ToString(callSuper = true)
	public class DemoEventAB extends DemoEventA {
	
		@Getter
		private String person;
		
		public DemoEventAB(String message, String person) {
			super(message);
			
			this.person = person;
		}
		
	};
	
	@ToString
	public class DemoEventB extends Event {
		
		@Getter
		private int number;
		
		public DemoEventB(int number) {
			this.number = number;
		}
		
	};
	
}
