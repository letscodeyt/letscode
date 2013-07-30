package net.letscode.game.api.entity;

/**
 * Defines an abstract controller; that is, a set of requirements needed to
 * fulfill some role for an Entity. These requirements are generally zone
 * specific; for example, a {@code ChatZone} might require a
 * {@code ChatController} to give an entity chat functionality. These should
 * remain pluggable; that is, it should be possible to change the implementation
 * of a controller on demand.
 * <p>Controllers have a set lifecycle, particularly in that they should be
 * expected to be able to activate, deactivate, and potentially reactivate at
 * any time. Additionally, the entity upon which they should operate may change
 * when reactivated, so this should not be assumed.</p>
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
