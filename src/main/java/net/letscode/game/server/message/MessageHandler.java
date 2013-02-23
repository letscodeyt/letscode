package net.letscode.game.server.message;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a MessageHandler annotation. These will be scanned at startup and
 * messages with {@code type} fields matching 
 * @author timothyb89
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MessageHandler {
	
	public String value();
	
}
