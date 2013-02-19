package net.letscode.game.server;

import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author timothyb89
 */
public class ClientHandlerServlet extends WebSocketServlet {

	private Logger logger = LoggerFactory.getLogger(ClientHandlerServlet.class);
	
	@Override
	public void configure(WebSocketServletFactory factory) {
		factory.register(ClientSession.class);
		
		// this doesn't seem to work yet (jetty 9M5)
		factory.getPolicy().setIdleTimeout(TimeUnit.MINUTES.toMillis(5));
	}
	
}
