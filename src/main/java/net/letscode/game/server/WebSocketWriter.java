package net.letscode.game.server;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import org.eclipse.jetty.websocket.api.WebSocketConnection;

/**
 * A {@link Writer} implementation for a {@link WebSocketConnection}, because
 * Jetty's is unimplemented.
 * @author timothyb89
 */
public class WebSocketWriter extends Writer {
	
	private WebSocketConnection socket;
	private Charset charset;
	
	public WebSocketWriter(WebSocketConnection socket) {
		this(socket, Charset.defaultCharset().name());
	}
	
	public WebSocketWriter(WebSocketConnection socket, String charset) {
		this(socket, Charset.forName(charset));
	}
	
	public WebSocketWriter(WebSocketConnection socket, Charset charset) {
		this.socket = socket;
		this.charset = charset;
	}
	
	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		System.out.print(new String(cbuf));
		
		ByteBuffer buf = charset.encode(CharBuffer.wrap(cbuf));
		socket.write(buf);
	}

	@Override
	public void flush() throws IOException {
		// do nothing, websockets are async
	}

	@Override
	public void close() throws IOException {
		socket.close();
	}
	
	
}
