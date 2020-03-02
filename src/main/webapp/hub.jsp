<!DOCTYPE html>

<html>
<%@ page import="it.is.free.hub.SendServlet" %>
  <head>
    <title>Hub Test Client</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
  </head>
  <body>
    <p>Hub demo</p>
    <form id="hub-form">
      <textarea id="hub-text" placeholder="Enter some text..."></textarea>
      <button type="submit">Send</button>
    </form>

    <div>
      <p>Messages:</p>
      <ul id="hub-response"></ul>
    </div>

    <div>
      <p>Status:</p>
      <ul id="hub-status"></ul>
    </div>
    <script
			  src="https://code.jquery.com/jquery-2.2.4.min.js"
			  integrity="sha256-BbhdlvQf/xTY9gja0Dq3HiwQF8LaCRTXxZKRutelT44="
			  crossorigin="anonymous"></script>
    <script>
    $(function() {
      var webSocketUri =  "<%=SendServlet.getWebSocketAddress() %>";

      /* Get elements from the page */
      var form = $('#hub-form');
      var textarea = $('#hub-text');
      var output = $('#hub-response');
      var status = $('#hub-status');

      /* Helper to keep an activity log on the page. */
      function log(text){
        status.append($('<li>').text(text))
      }

      /* Establish the WebSocket connection and register event handlers. */
      var websocket = new WebSocket(webSocketUri);

      websocket.onopen = function() {
        log('Connected : ' + webSocketUri);
      };

      websocket.onclose = function() {
        log('Closed');
      };

      websocket.onmessage = function(e) {
        log('Message received');
        output.append($('<li>').text(e.data));
      };

      websocket.onerror = function(e) {
        log('Error (see console)');
        console.log(e);
      };

      /* Handle form submission and send a message to the websocket. */
      form.submit(function(e) {
        e.preventDefault();
        var data = textarea.val();
        websocket.send(data);
      });
    });
    </script>
  </body>
</html>
