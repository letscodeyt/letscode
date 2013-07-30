package net.letscode.game.server.message;

/**
 * An exception thrown when an incoming message from the client could not be
 * parsed. In particular, this exception implies that, while the message was
 * valid JSON, and had a {@code type} field that corresponded to a message
 * handler, some additional field within the message was either missing or
 * contained invalid data.
 * <p>Side note: this exception is required to extend {@code RuntimeException}
 * as the only classes expected to throw it are being instantiated via
 * reflection.</p>
 * @author timothyb89
 */
public class MalformedMessageException extends RuntimeException {

	/**
	 * Creates a new instance of
	 * <code>MalformedMessageException</code> without detail message.
	 */
	public MalformedMessageException() {
	}

	/**
	 * Constructs an instance of
	 * <code>MalformedMessageException</code> with the specified detail message.
	 *
	 * @param msg the detail message.
	 */
	public MalformedMessageException(String msg) {
		super(msg);
	}
}
