package net.letscode.game.server.message.request.handler;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.letscode.game.config.Config;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles runtime classpath scanning for {@code @RequestHandler}-
 * annotated classes, and also allows them to be instantiated by name.
 * 
 * <p>Classpath scanning specifically looks for @{@link RequestHandler}
 * annotations at the locations configured in
 * {@code Config.get().prefixes.requestHandlers}. Handlers may also be
 * registered manually with {@link register(Class)} (which must be annotated),
 * or with {@link register(String, Class)} (which does not require an
 * annotation).</p>
 * 
 * @author timothyb89
 */
public class RequestHandlerFactory {
	
	private static RequestHandlerFactory instance;
	
	private Logger logger = LoggerFactory.getLogger(RequestHandlerFactory.class);
	
	private Map<String, Class<?>> handlers;
	
	private RequestHandlerFactory() {
		handlers = new HashMap<String, Class<?>>();
		
		initHandlers();
	}
	
	/**
	 * Gets the singleton instance of the {@code RequestHandlerFactory}. If no
	 * instance currently exists, one will be created.
	 * @return the current instance
	 */
	public static RequestHandlerFactory get() {
		if (instance == null) {
			instance = new RequestHandlerFactory();
		}
		
		return instance;
	}

	private void initHandlers() {
		// build the filters
		FilterBuilder filters = new FilterBuilder();
		for (String s : Config.get().prefixes.requestHandlers) {
			filters.include(s);
		}
		
		// build the necessary URLs
		// these URLs are just parts of the classpath containing the given
		// package, and can also include classes we don't want (thus the filter)
		Set<URL> urls = new HashSet<URL>();
		for (String s : Config.get().prefixes.requestHandlers) {
			urls.addAll(ClasspathHelper.forPackage(s));
		}
		
		// init Reflections
		Reflections r = new Reflections(new ConfigurationBuilder()
				.filterInputsBy(filters)
				.setUrls(urls)
				.setScanners(new TypeAnnotationsScanner()));
		
		// find the annotated types
		Set<Class<?>> annotated = r.getTypesAnnotatedWith(RequestHandler.class);
		
		// iterate over each class, and register it
		for (Class<?> c : annotated) {
			register(c);
		}
	}
	
	/**
	 * Registers a request handler manually. Classes here have no annotation
	 * requirements as the name is provided manually; however, they still must
	 * meet all constructor-related requirements in order to be instantiated
	 * properly. Note that the {@code requestName} parameter here will override
	 * any name values defined in a {@code @RequestHandler} annotation.
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
	 * Attempts to register the given class as a request handler. Classes here
	 * must be annotated with @{@link RequestHandler}, otherwise an
	 * {@link IllegalArgumentException} will be thrown. To avoid this, use
	 * {@link register(String, Class)} instead.
	 * @param handlerClass the class to register
	 */
	public void register(Class<?> handlerClass) {
		// make sure the annotation exists
		if (handlerClass.isAnnotationPresent(RequestHandler.class)) {
			// get the annotaton
			RequestHandler h = handlerClass.getAnnotation(RequestHandler.class);
			
			// get the defined handler name and register the class
			register(h.value(), handlerClass);
		} else {
			// looks like somebody should've used the other method
			throw new IllegalArgumentException(handlerClass
					+ " has no @RequestHandler annotation defined,"
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
