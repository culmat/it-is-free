
package it.is.free.hub;

import static java.lang.String.format;

import java.io.IOException;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

@SuppressWarnings("serial")
@WebServlet(name = "Hub WebSocket Servlet", urlPatterns = { "/hub/*" })
public class HubServlet extends WebSocketServlet implements WebSocketCreator {

	private final String split;

	public HubServlet() {
		split = getClass().getAnnotation(WebServlet.class).urlPatterns()[0].replace("*", "");
	}

	@Override
	public void configure(WebSocketServletFactory factory) {
		factory.setCreator(this);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/plain");
		for ( Entry<String, Set<Session>> e : ServerSocket.sessions.entrySet()) {
			resp.getOutputStream().write(format("%s(%s)\n", e.getKey(), e.getValue().size()).getBytes());
		}
		if(ServerSocket.sessions.isEmpty()) {
			resp.getOutputStream().write("no active sessions\n".getBytes());
		}
		resp.getOutputStream().write(("\n"+new Date()+"\n").getBytes());
	}

	@Override
	public Object createWebSocket(ServletUpgradeRequest servletUpgradeRequest,
			ServletUpgradeResponse servletUpgradeResponse) {
		return new ServerSocket(getSessionID(servletUpgradeRequest.getRequestPath()));
	}

	private String getSessionID(String requestPath) {
		String[] tokens = requestPath.split(split, 2);
		return tokens.length > 1 ? tokens[1] : "";
	}
}
