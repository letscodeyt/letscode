package net.letscode.game.server.message.response.handler;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import net.letscode.game.config.Config;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

/**
 * A factory to manage the registration of all {@link ResponseHandler}s.
 * Handlers may be registered either manually via the {@link register(Class)}
 * method, or automatically if they are annotated and within a scanning package
 * specified in {@link Config}'s {@code prefixes.responseHandlers} field.
 * @author timothyb89
 */
@Slf4j
public class ResponseHandlerFactory {
	
	private static ResponseHandlerFactory instance;
	
	private Map<String, Class<?>> handlers;
	
	private ResponseHandlerFactory() {
		handlers = new HashMap<>();
		
		initHandlers();
	}
	
	/**
	 * Gets the singleton instance of the {@code ResponseHandlerFactory}. If no
	 * instance currently exists, one will be created.
	 * @return the current instance
	 */
	public static ResponseHandlerFactory get() {
		if (instance == null) {
			instance = new ResponseHandlerFactory();
		}
		
		return instance;
	}

	private void initHandlers() {
		// build the URLs and filters
		// these URLs are just parts of the classpath containing the given
		// package, and can also include classes we don't want (thus the filter)
		Set<URL> urls = new HashSet<>();
		FilterBuilder filters = new FilterBuilder();
		for (String s : Config.get().prefixes.messageHandlers) {
			urls.addAll(ClasspathHelper.forPackage(s));
			filters = filters.include(FilterBuilder.prefix(s + "."));
		}
		
		// init Reflections
		Reflections r = new Reflections(new ConfigurationBuilder()
				.filterInputsBy(filters)
				.setUrls(urls)
				.setScanners(new TypeAnnotationsScanner()));
		
		// find the annotated types
		Set<Class<?>> annotated = r.getTypesAnnotatedWith(ResponseHandler.class);
		
		// iterate over each class, and register it
		for (Class<?> c : annotated) {
			register(c);
		}
		
		log.info("Registered " + + annotated.size() + " response handlers.");
	}
	
	/**
	 * Registers a request handler manually. Classes here have no annotation
	 * requirements as the name is provided manually; however, they still must
	 * meet all constructor-related requirements in order to be instantiated
	 * properly. Note that the {@code requestName} parameter here will override
	 * any name values defined in a {@code @ResponseHandler} annotation.
	 * 
	 * <p><b>Warning:</b> previous handlers registered with the same name will
	 * be replaced with {@code handlerClass}.</p>
	 * 
	 * @param requestName The value of the {@code name} field of requests
	 *     handled by this class.
	 * @param handlerClass The class to register
	 */
	public void register(String requestName, Class handlerClass) {
		handlers.put(requestName, handlerClass);
	}
	
	/**
	 * Attempts to register the given class as a response handler. Classes here
	 * must be annotated with @{@link ResponseHandler}, otherwise an
	 * {@link IllegalArgumentException} will be thrown. To avoid this, use
	 * {@link register(String, Class)} instead.
	 * @param handlerClass the class to register
	 */
	public void register(Class<?> handlerClass) {
		// make sure the annotation exists
		if (handlerClass.isAnnotationPresent(ResponseHandler.class)) {
			// get the annotaton
			ResponseHandler h = handlerClass.getAnnotation(ResponseHandler.class);
			
			// get the defined handler name and register the class
			register(h.value(), handlerClass);
		} else {
			// looks like somebody should've used the other method
			throw new IllegalArgumentException(handlerClass
					+ " has no @ResponseHandler annotation defined,"
					+ " use register(String, Class) instead.");
		}
	}
	
	/**
	 * Attempts to retrieve a request handler registered with the given name. If
	 * no handler with the given name has been registered, {@code null} is
	 * returned.
	 * @param requestName the request name for which to retrieve a handler.
	 * @return a handler for the given request name, or null.
	 */
	public Class<?> getHandler(String requestName) {
		return handlers.get(requestName);
	}
	
}
