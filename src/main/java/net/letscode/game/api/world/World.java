package net.letscode.game.api.world;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.letscode.game.api.zone.Zone;
import net.letscode.game.api.zone.chat.ChatZone;
import net.letscode.game.config.Config;
import net.letscode.game.event.EventBus;
import net.letscode.game.event.EventBusClient;
import net.letscode.game.event.EventBusProvider;

/**
 * Defines the game world. The world is, primarily, a container for Zone
 * objects.
 * @author timothyb89
 */
@Slf4j
public class World implements EventBusProvider {
	
	private static World instance;
	
	private List<Zone> zones;
	
	/**
	 * The default zone, where all new entities should be put if they don't have
	 * a specifically defined zone.
	 */
	@Getter
	@Setter
	private Zone defaultZone;
	
	private EventBus bus;
	
	private boolean killed;
	private long lastTick;
	
	private World() {
		zones = new ArrayList<>();
		
		bus = new EventBus() {{
			add(WorldTickEvent.class);
			add(ZoneAddedEvent.class);
			add(ZoneRemovedEvent.class);
		}};
		
		// a quick and dirty singleton
		initZones();
	}
	
	public static World get() {
		if (instance == null) {
			instance = new World();
		}
		
		return instance;
	}
	
	private void initZones() {
		// TODO: do real zone initialization at some point
		// right now, we'll just add a dummy chatzone for testing
		
		defaultZone = new ChatZone();
		addZone(new ChatZone());
	}
	
	@Override
	public EventBusClient bus() {
		return bus.getClient();
	}
	
	/**
	 * Starts the world processing thread.
	 */
	public void start() {
		killed = false;
		
		new Thread(new WorldThread(), "WorldThread").start();
	}
	
	public List<Zone> getZones() {
		return zones; // TODO: probably should be unmodifiable
	}
	
	public void addZone(Zone zone) {
		zones.add(zone);
		bus.push(new ZoneAddedEvent(zone));
	}
	
	public void removeZone(Zone zone) {
		zones.remove(zone);
		bus.push(new ZoneRemovedEvent(zone));
	}
	
	protected void tick() {
		long tickRate = Config.get().server.tickRate;
		long timestamp = System.currentTimeMillis();
		long elapsed = timestamp - lastTick;
		long deadline = timestamp + tickRate;
		
		// puch the tick event to the event bus
		// this is synchronous and will return after all listeners have been
		// processed
		bus.push(new WorldTickEvent(
				timestamp, elapsed, tickRate, deadline));
		
		long end = System.currentTimeMillis();
		long diff = end - deadline;
		
		if (diff < 0) {
			log.warn("Tick duration overflow! {} ms", diff);
		} else {
			// sleep the remaining time to prevent CPU hammering
			// TODO: make this toggleable?
			try {
				Thread.sleep(diff);
			} catch (InterruptedException ex) {
				// ignore
			}
		}
		
		lastTick = timestamp;
	}
	
	private class WorldThread implements Runnable {

		@Override
		public void run() {
			lastTick = System.currentTimeMillis();
			
			while (!killed) {
				tick();
			}
		}
		
	}
	
}
