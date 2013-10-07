package net.letscode.game.api.controller;

import net.letscode.game.api.entity.Entity;

/**
 * Defines an abstract controller; that is, a set of requirements needed to
 * fulfill some role for an Entity. These requirements are generally zone
 * specific; for example, a {@code ChatZone} might require a
 * {@code ChatController} to give an entity chat functionality. These should
 * remain pluggable; that is, it should be possible to change the implementation
 * of a controller on demand.
 * <p>Generally speaking, controllers handle the communication between an entity
 * and the logic responsible for controlling whatever aspect of its behavior the
 * controller implements. That is, a {@code PlayerChatController} would be
 * responsible not only for pushing incoming chat messages from players into
 * their zones, but would also be responsible for pushing messages in the zone
 * back to the player. Conversely, entity controllers implementing some aspect
 * of entity AI would be responsible for updating the AI state about zone
 * changes.</p>
 * <p>In practice, this means that controllers will need to receive events from
 * both their "client" agent, like player input events, as well as events from
 * the zone that the entity is in. See
 * {@link net.letscode.game.api.controller.player.PlayerChatController} as an
 * example.</p>
 * <p>Additionally, controllers have a set lifecycle, particularly in that they
 * should be expected to be able to activate, deactivate, and potentially
 * reactivate at any time, with any given (and possibly different) entity.
 * However, controllers may be "anchored" to a particular "controlling agent";
 * that is, a controller for player input can be permanently bound to a
 * {@code ClientSession} - the lifecycle requirements only exist to ensure
 * entity ambiguity.</p>
 * @author timothyb89
 */
public interface Controller {
	
	/**
	 * Called when this Controller has been activated for the given entity. This
	 * can be used to register any necessary listeners or to otherwise prepare
	 * for use.
	 * @param e the entity that has started using this controller
	 */
	public abstract void onActivated(Entity e);
	
	/**
	 * Called when this Controller has been deactivated for the given entity.
	 * This can be used to deregister any listeners or to otherwise clean up.
	 * @param e the entity that has stopped using this controller
	 */
	public abstract void onDeactivated(Entity e);
	
}
