package net.letscode.game.server.message;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a MessageHandler annotation. These will be scanned at startup, and
 * incoming messages with {@code type} fields matching the value of this
 * annotation will be notified of the message.
 * @author timothyb89
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MessageHandler {
	
	public String value();
	
}
