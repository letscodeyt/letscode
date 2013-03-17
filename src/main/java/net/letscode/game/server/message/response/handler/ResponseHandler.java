package net.letscode.game.server.message.response.handler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a {@code @ResponseHandler} annotation. Classes with this annotation
 * are scanned at startup and registered with the
 * {@link ResponseHandlerFactory}.
 * @author timothyb89
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ResponseHandler {
	
	/**
	 * Gets the value of the {@code name} field on an incoming request that must
	 * be matched to trigger this {@code ResponseHandler}.
	 * @return the request name field to match
	 */
	public String value();
	
}
