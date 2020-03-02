package it.is.free.hub;

import static java.lang.String.format;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;


@WebSocket(maxTextMessageSize = 64 * 1024)
public class ServerSocket {
	private Logger logger = Logger.getLogger(SendServlet.class.getName());
	private Session session;
    static ConcurrentMap<String,Set<Session>> sessions = new ConcurrentHashMap<String,Set<Session>>() {
		public java.util.Set<Session> get(Object key) {
			Set<Session> ret = super.get(key);
			if(ret == null) {
				ret = new HashSet<>();
				put(key.toString(), ret);
			}
			return ret;
		};
	};
	
	private final String sessionId;

	public ServerSocket(String sessionId) {
		this.sessionId = sessionId;
	}

	@OnWebSocketConnect
	public void onWebSocketConnect(Session session) {		
		this.session = session;
		sendMessage(session, format("{\"type\" : \"init-connection\", \"peerCount\" : %s}", sessions.get(sessionId).size()));
		sessions.get(sessionId).add(session);
		logger.fine("Socket Connected: " + session);
	}

	private void sendMessage(Session session, String message) {
		synchronized (session) {
			try {
				session.getRemote().sendString(message);
			} catch (IOException e) {
				logger.fine("Sending message: " + e.getMessage());
			}
		}
	}

	@OnWebSocketMessage
	public void onWebSocketText(String message) {
		logger.fine("Received message: " + message);
		for (Session session : sessions.get(sessionId)) {
			sendMessage(session, message);
		}
	}

	@OnWebSocketClose
	public void onWebSocketClose(int statusCode, String reason) {
		Set<Session> set = sessions.get(sessionId);
		if(set.size() < 2) {
			sessions.remove(sessionId);
		} else {			
			set.remove(session);
		}
		logger.fine("Socket Closed: [" + statusCode + "] " + reason);
	}

	@OnWebSocketError
	public void onWebSocketError(Throwable cause) {
		logger.severe("Websocket error : " + cause.getMessage());
	}
}
