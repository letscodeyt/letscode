package net.letscode.game.api.world;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.letscode.game.api.zone.Zone;
import net.letscode.game.api.zone.chat.ChatZone;

/**
 * Defines the game world. The world is, primarily, a container for Zone
 * objects.
 * @author timothyb89
 */
public class World {
	
	private static World instance;
	
	private List<Zone> zones;
	
	/**
	 * The default zone, where all new entities should be put if they don't have
	 * a specifically defined zone.
	 */
	@Getter
	@Setter
	private Zone defaultZone;
	
	private World() {
		zones = new ArrayList<>();
		
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
	
	public List<Zone> getZones() {
		return zones; // TODO: probably should be unmodifiable
	}
	
	public void addZone(Zone zone) {
		zones.add(zone);
	}
	
	public void removeZone(Zone zone) {
		zones.remove(zone);
	}
	
}
