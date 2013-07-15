package net.letscode.game.server;

import com.fasterxml.jackson.databind.JsonNode;
import net.letscode.game.api.util.JsonSerializable;

/**
 * A small interface used to listen for message events that occur within a
 * {@link ClientSession}
 * @author timothyb89
 */
public interface SessionListener {
	
	/**
	 * Called after an outgoing message has been sent to the client.
	 * @param client The ClientSession in which the event occurred
	 * @param s The message that was sent
	 */
	public void onMessageSent(ClientSession client, JsonSerializable s);
	
	/**
	 * Called when a message has been received from a client.
	 * @param client The ClientSession in which the event occurred
	 * @param node The (parsed) message that was received
	 */
	public void onMessageReceived(ClientSession client, JsonNode node);
	
}
