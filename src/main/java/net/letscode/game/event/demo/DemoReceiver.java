package net.letscode.game.event.demo;

import lombok.extern.slf4j.Slf4j;
import net.letscode.game.event.EventHandler;
import net.letscode.game.event.EventPriority;
import net.letscode.game.event.demo.DemoEmitter.DemoEventA;
import net.letscode.game.event.demo.DemoEmitter.DemoEventAB;
import net.letscode.game.event.demo.DemoEmitter.DemoEventB;

/**
 * A simple receiver class for events emitted by {@link DemoEmitter}
 * @author timothyb89
 */
@Slf4j
public class DemoReceiver {
	
	@EventHandler(priority = EventPriority.LOW)
	public void onEventALow(DemoEventA event) {
		// this method should receive all A and AB-typed events after onEventAHigh()
		log.info("onEventALow(): " + event);
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onEventAHigh(DemoEventA event) {
		// this method should receive all A and AB-typed events before onEventALow()
		log.info("onEventAHigh(): " + event);
	}
	
	@EventHandler // priority defaults to 'normal' ( 0 )
	public void onEventAB(DemoEventAB event) {
		// this method should receive all events of type AB, but not A
		log.info("onEventAB(): " + event);
	}
	
	@EventHandler
	public void onEventB(DemoEventB event) {
		log.info("onEventB(): " + event);
	}
	
}
