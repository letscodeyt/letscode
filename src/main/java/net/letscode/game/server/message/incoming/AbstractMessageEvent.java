package net.letscode.game.server.message.incoming;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import net.letscode.game.event.Event;
import net.letscode.game.server.client.ClientSession;
import net.letscode.game.server.message.MalformedMessageException;
import net.letscode.game.server.message.MessageDispatcher;

/**
 * Defines an abstract message event, specifically intended for events that
 * trigger on reception of a message, and that are constructed automatically
 * via {@link MessageDispatcher}.
 * @author timothyb89
 */
public abstract class AbstractMessageEvent extends Event {
	
	@Getter
	private final ClientSession session;
	
	@Getter
	private final JsonNode node;

	public AbstractMessageEvent(ClientSession session, JsonNode node) {
		this.session = session;
		this.node = node;
	}
	
	/**
	 * Verifies that the given field is textual, not null or missing, and not
	 * empty. If any of these is true, a {@link MalformedMessageException} is
	 * thrown, otherwise the text value of the field is returned.
	 * @param field the field to check
	 * @param allowEmpty if true, allows the empty string ("")
	 * @return the value if the field validates
	 */
	protected String validateTextField(JsonNode field, boolean allowEmpty) {
		if (field.isMissingNode() || field.isNull()) {
			throw new MalformedMessageException("Field is missing or null.");
		}
		
		if (!field.isTextual()) {
			throw new MalformedMessageException(
					"Found a non-textual field where text was expected.");
		}
		
		if (!allowEmpty && field.asText().isEmpty()) {
			throw new MalformedMessageException(
					"Found an empty text field where a value was expected.");
		}
		
		return field.asText();
	}
	
	/**
	 * Validates that the given text field is defined, not null, text, and
	 * not empty.
	 * @see #validateTextField(JsonNode, boolean) 
	 * @param field the field to check
	 * @return the value, if the field validates
	 */
	protected String validateTextField(JsonNode field) {
		return validateTextField(field, false);
	}
	
}
