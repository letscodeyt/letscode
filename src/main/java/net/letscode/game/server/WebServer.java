package net.letscode.game.server;

import java.io.File;
import lombok.extern.slf4j.Slf4j;
import net.letscode.game.api.world.World;
import net.letscode.game.auth.shiro.MongoRealm;
import net.letscode.game.config.Config;
import net.letscode.game.db.Database;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * The WebServer class - essentially the main class for the server. This handles
 * initialization and management of the Jetty process that the server runs off
 * of.
 * @author timothyb89
 */
@Slf4j
public class WebServer {
	
	private static WebServer instance;
	
	private Database db;
	
	private Server server;
	
	public WebServer() {
		int port = Config.get().server.port;
		
		log.info("Initializing database...");
		db = Database.get();
		
		log.info("Initializing authentication...");
		MongoRealm realm = new MongoRealm();
		DefaultSecurityManager sm = new DefaultSecurityManager(realm);
		SecurityUtils.setSecurityManager(sm);
		
		log.info("Initializing game world...");
		World.get();
		
		log.info("Starting server on port " + port);
		server = new Server(port);
		initConnectors();
		
		try {
			server.start();
			log.info("Server opened.");
		} catch (Exception ex) {
			log.error("Failed to start server", ex);
		}
	}

	public static WebServer get() {
		if (instance == null) {
			instance = new WebServer();
		}
		
		return instance;
	}
	
	private void initConnectors() {
		File dir = new File(Config.get().server.webDirectory);
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("Invalid web directory: " + dir);
		}
		
		HandlerList handlers = new HandlerList();
		
		// a handler to serve files from the ./web/ directory
		ResourceHandler files = new ResourceHandler();
		files.setDirectoriesListed(true);
		files.setWelcomeFiles(new String[] { "index.html" });
		log.info("Web directory: " + dir.getAbsolutePath());
		files.setResourceBase("./web/");
		handlers.addHandler(files);
		
		// the websocket, at /clients
		ServletContextHandler wsServlet = new ServletContextHandler();
		wsServlet.addServlet(ClientHandlerServlet.class, "/");
		wsServlet.setContextPath("/clients");
		handlers.addHandler(wsServlet);
		
		server.setHandler(handlers);
	}
	
}
