# Chat Example #


A simple chat application over HTTP and WebSocket with jQuery Stream and various server side technologies. The chat client is pure HTML web page and named [index.html](http://code.google.com/p/jquery-stream/source/browse/sub-projects/jquery-stream-servlet/trunk/src/main/webapp/index.html). jQuery 1.5.0 and jQuery Stream 1.2 are used by default.

I strongly recommend you to read ServerSideProcessing wiki before browsing server-side source.

## Java Servlet 3.0 ##

[Browse source](http://code.google.com/p/jquery-stream/source/browse/#svn%2Fsub-projects%2Fjquery-stream-servlet%2Ftrunk)

Servlet 3.0 (JSR 351) is a specification that is part of the Java EE 6 technologies and includes support for asynchronous processing of request helping develop interactive applications with server push.

**Highlight**

  * [ChatServlet.java](http://code.google.com/p/jquery-stream/source/browse/sub-projects/jquery-stream-servlet/trunk/src/main/java/flowersinthesand/example/ChatServlet.java) - Asynchronous servlet.

**Note**

  * Tested with Tomcat 7.0.11 and Jetty 8.0.0.M3
  * It only works over HTTP, because there is no support for WebSocket in Servlet 3.0 spec.
  * `enableXDR` is true
  * Servlet 3.0 containers: Tomcat 7, Jetty 8, Glassfish 3, Resin 4 and JBoss AS 7.

## Jetty 8 ##

[Browse source](http://code.google.com/p/jquery-stream/source/browse/#svn%2Fsub-projects%2Fjquery-stream-jetty%2Ftrunk)

[Jetty](http://www.eclipse.org/jetty/) is a Java-based HTTP server and servlet container. Jetty 8 is Servlet 3.0 containers and provides WebSocket implementation, so that it is possible to offer server push via both HTTP and WebSocket protocol. Jetty provides WebSocket implementation as a subclass of HttpServlet.

**Highlight**

  * [ChatServlet.java](http://code.google.com/p/jquery-stream/source/browse/sub-projects/jquery-stream-jetty/trunk/src/main/java/flowersinthesand/example/ChatServlet.java) - Asynchronous servlet extending `WebSocketServlet`.

**Note**

  * Tested with Jetty 8.0.0.M3
  * `enableXDR` is true
  * It works over both HTTP and WebSocket.

## Atmosphere ##

[Browse source](http://code.google.com/p/jquery-stream/source/browse/#svn%2Fsub-projects%2Fjquery-stream-atmosphere%2Ftrunk)

[Atmosphere](http://atmosphere.java.net/) is a WebSocket/Comet web framework that enables real time web application in Java. Atmosphere really simplifies a real time web application development and works with servlet containers that do not implement Servlet 3.0 but nativley support Comet such as Tomcat 6.

**Highlight**

  * [ChatAtmosphereHandler.java](http://code.google.com/p/jquery-stream/source/browse/sub-projects/jquery-stream-atmosphere/trunk/src/main/java/flowersinthesand/example/ChatAtmosphereHandler.java) - POJO Atmosphere handler.
  * [jquery.stream.atmosphere.js](http://code.google.com/p/jquery-stream/source/browse/sub-projects/jquery-stream-atmosphere/trunk/src/main/webapp/jquery.stream.atmosphere.js) - jQuery Stream plugin for Atmosphere.
  * [index.html](http://code.google.com/p/jquery-stream/source/browse/sub-projects/jquery-stream-atmosphere/trunk/src/main/webapp/index.html) - Chat client using jQuery Stream plugin for Atmosphere.

**Note**

  * Used Atmosphere 0.7.2
  * Tested with Tomcat 6.0.20, Tomcat 7.0.11 and Jetty 8.0.0.M3
  * jQuery Stream plugin for Atmosphere
  * It seems that activating WebSocket needs configurations according to each servlet container.