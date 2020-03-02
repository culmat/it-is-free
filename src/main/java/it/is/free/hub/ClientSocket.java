package it.is.free.hub;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Logger;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**
 * Basic Client Socket.
 */
@WebSocket(maxTextMessageSize = 64 * 1024)
public class ClientSocket {
  private Logger logger = Logger.getLogger(ClientSocket.class.getName());
  private Session session;
  // stores the messages in-memory.
  // Note : this is currently an in-memory store for demonstration,
  // not recommended for production use-cases.
  private static Collection<String> messages = new ConcurrentLinkedDeque<>();

  @OnWebSocketClose
  public void onClose(int statusCode, String reason) {
    logger.fine("Connection closed: " + statusCode + ":" + reason);
    this.session = null;
  }

  @OnWebSocketConnect
  public void onConnect(Session session) {
    this.session = session;
  }

  @OnWebSocketMessage
  public void onMessage(String msg) {
    logger.fine("Message Received : " + msg);
    messages.add(msg);
  }

  // Retrieve all received messages.
  public static Collection<String> getReceivedMessages() {
    return Collections.unmodifiableCollection(messages);
  }
}
