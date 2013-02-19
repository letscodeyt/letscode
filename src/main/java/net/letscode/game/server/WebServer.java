package net.letscode.game.server;

import java.io.File;
import net.letscode.game.auth.shiro.MongoRealm;
import net.letscode.game.config.Config;
import net.letscode.game.db.Database;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.Subject;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author timothyb89
 */
public class WebServer {
	
	private Logger logger = LoggerFactory.getLogger(WebServer.class);
	
	private Database db;
	
	private Server server;
	
	public WebServer() {
		int port = Config.get().server.port;
		
		logger.info("Initializing database...");
		db = Database.get();
		
		
		logger.info("Initializing authentication...");
		MongoRealm realm = new MongoRealm();
		DefaultSecurityManager sm = new DefaultSecurityManager(realm);
		SecurityUtils.setSecurityManager(sm);
		
		logger.info("Starting server on port " + port);
		server = new Server(port);
		initConnectors();
		
		try {
			server.start();
			logger.info("Server opened.");
		} catch (Exception ex) {
			logger.error("Failed to start server", ex);
		}
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
		logger.info("Web directory: " + dir.getAbsolutePath());
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
