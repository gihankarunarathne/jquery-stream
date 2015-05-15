**Please, see ChatExample for practical use. This wiki will not be updated.**

# Echo Example #


## Web Page ##
Web Page is an echo client.

Whenever user enters a message, the page makes JavaScript object containing it and send it to the echo server using the jQuery Stream, and when a message returns from the server, the page displays it.

According to the server support, the stream type may have to be set to `http` and its URL may have to be modified.

Since the web page is plain HTML page and the jQuery Stream is plain JavaScript library, they don't need any server-side support, so choose a server implementation in [WebSocket/HTTP Echo Server](#WebSocket_/HTTP_Echo_Server.md) according to your preference.

`/echo.html`
```
<!DOCTYPE html>
<html>
    <head>
        <title>Echo</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <script type="text/javascript" src="./js/jquery-1.4.1.js"></script>
        <script type="text/javascript" src="./js/jquery.stream.js"></script>
        <script type="text/javascript">
        $.stream.setup({enableXDR: true});
        
        $(function() {
            $.stream("./echo", {
                open: function() {
                    $("#textfield").removeAttr("disabled").focus();
                },
                message: function(event) {
                    $("<p />").text(event.data).prependTo("#content");
                },
                error: function() {
                    $("#textfield").attr("disabled", "disabled");
                },
                close: function() {
                    $("#textfield").attr("disabled", "disabled");
                }
            });
            
            $("#textfield").keyup(function(event) {
                if (event.which === 13 && $.trim(this.value)) {
                    $.stream().send({message: this.value});
                    this.value = "";
                }
            });
        });
        </script>
        <style>
        body {padding: 0; margin: 0; font-family: 'Trebuchet MS','Malgun Gothic'; font-size: 62.5%; color: #333333}
        #editor {margin: 15px 25px;}
        #textfield {width: 100%; height: 28px; line-height: 28px; font-family: 'Trebuchet MS','Malgun Gothic'; 
                    border: medium none; border-color: #E5E5E5 #DBDBDB #D2D2D2; border-style: solid; border-width: 1px;}
        #content {height: 100%; overflow-y: auto; padding: 0 25px;}
        #content p {margin: 0; padding: 0; font-size: 1.3em; color: #444444; line-height: 1.7em; word-wrap: break-word;}
        </style>
    </head>
    <body>
        <div id="editor">
            <input id="textfield" type="text" disabled="disabled" />
        </div>
        <div id="content"></div>
    </body>
</html>
```

## WebSocket/HTTP Echo Server ##
WebSocket/HTTP Echo Server is just a typical echo server which sends back the client's message through ws or http protocol.

See ServerSideProcessing for details about what methods do what.

If you have implemented the server logic using technology or platform not listed, please annotate it and create a issue to append that code to this wiki.

### Java - Servlet 3.0 ###
The Servlet 3.0 specification includes support for asynchronous processing of request, but there is no support for WebSocket.

The following example runs with any servlet container implementing Servlet 3.0 such as Tomcat 7 and Jetty 8 only via HTTP protocol.

`flowersinthesand.example.EchoServlet`
```
package flowersinthesand.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Registers a servlet by newly introduced annotation, @WebServlet
@WebServlet(urlPatterns = "/chat", asyncSupported = true)
public class EchoServlet extends HttpServlet {

    private static final long serialVersionUID = -8823775068689773674L;

    private Map<String, AsyncContext> asyncContexts = new ConcurrentHashMap<String, AsyncContext>();

    // GET method is used to open stream
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Rejects WebSocket opening handshake
        if ("websocket".equalsIgnoreCase(request.getHeader("Upgrade"))) {
            response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
            return;
        }

        response.setCharacterEncoding("utf-8");

        // Content-Type header
        response.setContentType("text/plain");

        // Access-Control-Allow-Origin header
        response.setHeader("Access-Control-Allow-Origin", "*");

        PrintWriter writer = response.getWriter();

        // Id
        final String id = UUID.randomUUID().toString();
        writer.print(id);
        writer.print(';');

        // Padding
        for (int i = 0; i < 1024; i++) {
            writer.print(' ');
        }
        writer.print(';');
        writer.flush();

        // Starts asynchronous mode
        // AsyncContext, AsyncListener and AsyncEvent are used for asynchronous operation
        final AsyncContext ac = request.startAsync();
        ac.setTimeout(5 * 60 * 1000);
        ac.addListener(new AsyncListener() {
            public void onComplete(AsyncEvent event) throws IOException {
                asyncContexts.remove(id);
            }

            public void onTimeout(AsyncEvent event) throws IOException {
                asyncContexts.remove(id);
            }

            public void onError(AsyncEvent event) throws IOException {
                asyncContexts.remove(id);
            }

            public void onStartAsync(AsyncEvent event) throws IOException {

            }
        });

        // Manages AsyncContext instances by the id
        asyncContexts.put(id, ac);
    }

    // POST method is used to handle data sent by user via the stream
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");

        // Finds AsyncContext instance by stream id
        AsyncContext ac = asyncContexts.get(request.getParameter("metadata.id"));
        if (ac == null) {
            return;
        }

        // Close request means that browser closed this stream
        if ("close".equals(request.getParameter("metadata.type"))) {
            ac.complete();
            return;
        }

        String message = request.getParameter("message");
        PrintWriter writer = ac.getResponse().getWriter();

        // Sends message
        writer.print(message.length() + ";" + message + ";");
        writer.flush();
    }

}
```

### Java - Servlet 3.0 and Jetty 8 ###
Jetty is a servlet container, implements Servlet 3.0 and also provides WebSocket based on servlet.

Currently, Jetty 8.0.0 M3 does not seem to support new annotations.

`web.xml`
```
<web-app 
    xmlns="http://java.sun.com/xml/ns/javaee" 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd" 
    version="3.0">

    <servlet>
        <servlet-name>Echo</servlet-name>
        <servlet-class>flowersinthesand.example.EchoServlet</servlet-class>
        <async-supported>true</async-supported>
    </servlet>

    <servlet-mapping>
        <servlet-name>Echo</servlet-name>
        <url-pattern>/echo</url-pattern>
    </servlet-mapping>

</web-app>
```

`flowersinthesand.example.EchoServlet`
```
package flowersinthesand.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.util.UrlEncoded;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

// WebSocketServlet extending HttpServlet is base class
public class EchoServlet extends WebSocketServlet {

    private static final long serialVersionUID = -8823775068689773674L;

    private Map<String, AsyncContext> asyncContexts = new ConcurrentHashMap<String, AsyncContext>();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/plain");
        response.setHeader("Access-Control-Allow-Origin", "*");

        PrintWriter writer = response.getWriter();

        final String id = UUID.randomUUID().toString();
        writer.print(id);
        writer.print(';');

        for (int i = 0; i < 1024; i++) {
            writer.print(' ');
        }
        writer.print(';');
        writer.flush();

        final AsyncContext ac = request.startAsync();
        ac.setTimeout(5 * 60 * 1000);
        ac.addListener(new AsyncListener() {
            public void onComplete(AsyncEvent event) throws IOException {
                asyncContexts.remove(id);
            }

            public void onTimeout(AsyncEvent event) throws IOException {
                asyncContexts.remove(id);
            }

            public void onError(AsyncEvent event) throws IOException {
                asyncContexts.remove(id);
            }

            public void onStartAsync(AsyncEvent event) throws IOException {

            }
        });
        asyncContexts.put(id, ac);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");

        AsyncContext ac = asyncContexts.get(request.getParameter("metadata.id"));
        if (ac == null) {
            return;
        }

        if ("close".equals(request.getParameter("metadata.type"))) {
            ac.complete();
            return;
        }

        String message = request.getParameter("message");
        PrintWriter writer = ac.getResponse().getWriter();

        writer.print(message.length() + ";" + message + ";");
        writer.flush();
    }

    // Handles WebSocket connection
    @Override
    public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol) {

        // WebSocket for receiving text messages
        return new WebSocket.OnTextMessage() {

            Connection connection;

            @Override
            public void onOpen(Connection connection) {
                this.connection = connection;
            }

            @Override
            public void onClose(int closeCode, String message) {

            }

            @Override
            public void onMessage(String data) {
                // Decodes query string
                UrlEncoded parameters = new UrlEncoded(data);
                try {
                    connection.sendMessage(parameters.getString("message"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        };
    }

}
```