package net.letscode.game.server.message.request.handler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a {@code @RequestHandler} annotation. Classes with this annotation
 * are scanned at startup and registered with the {@link RequestHandlerFactory}.
 * @author timothyb89
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RequestHandler {
	
	/**
	 * Gets the value of the {@code name} field on an incoming request that must
	 * be matched to trigger this {@code RequestHandler}.
	 * @return the request name field to match
	 */
	public String value();
	
}
