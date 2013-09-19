package net.letscode.game.api.view;

import net.letscode.game.api.util.JsonSerializable;
import net.letscode.game.api.zone.Zone;

/**
 * Defines a base View. A view is an abstract definition of a method for
 * representing an entity within a zone. Similar to Controllers, Views are
 * pluggable and are completely independent of the type of entity they may be
 * associated with. While an entity is expected to define its own views, Zones
 * should also implement a fallback View that can be used to provide a basic and
 * generic representation of entities that don't implicitly support rendering in
 * a particular zone type. For example, a 2D zone could define a fallback that
 * renders unsupported entities as an "error" sprite. 
 * <p>While views are defined server-side, the actual rendering logic is
 * fundamentally left to the client. When possible, entities should attempt to
 * support multiple types of views, allowing them to be properly represented
 * within a multitude of different zones, and providing the potential for client
 * fallback if a particular view type were to require some unsupported
 * capability.</p>
 * <p>Similarly, server-side Views exclusively define rendering parameters for
 * clients to follow. As an example, a View for a 2D zone might define a size
 * and sprite for clients to render, and perhaps animation parameters. Views
 * must additionally provide a unique <code>type</code> that can be used by
 * clients to disambiguate between rendering methods.</p>
 * <p>Unlike Controllers, Views are inherently public and are exposed to
 * clients via the {@link JsonSerializable} interface. The data generated will
 * be exposed to all entities capable of viewing the entity, as defined by the
 * parent zone.</p>
 * @author timothyb
 */
public interface View<T extends Zone> extends JsonSerializable {
	
	public String getType();
	
}
