package net.letscode.game.api.entity.twod;

import net.letscode.game.api.entity.Entity;

/**
 * Defines an entity capable of being viewed in 2D space. Note that this
 * explicitly excludes functionality relating to entity movement and position.
 * 
 * <p>While this class defines a number of useful facilities for {@link net.letscode.game.api.zone.twod.Zone2D}</p>
 * 
 * <p>Positional data, as well as the heading, velocity, and other data is kept
 * in the {@link net.letscode.game.api.zone.twod.EntityData2D} class.
 * Fundamentally, this is intended to allow entities to exist within multiple
 * zones while having different representations within each; however, by
 * convention, properties that are not inherent to the entity should be kept
 * separate regardless of any technical reason for separation.</p>
 * @author timothyb89
 */
public class Entity2D extends Entity {
	
}
