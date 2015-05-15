# Server-Side Processing #
## WebSocket ##
### Parsing query string ###
The object data is serialized into query string as if in jQuery ajax, therefore the server-side application should parse the query string to retrieve data.

**Example:** Creating a parameters map from a query string in Jetty 8 (Java web server)
```
@Override
// Called with a complete text message when all fragments have been received.
public void onMessage(String queryString) {
    Map<String, List<String>> parameters = parseQueryString(queryString);

    Map<String, String> data = new LinkedHashMap<String, String>();
    data.put("username", parameters.get("username").get(0));
    data.put("message", parameters.get("message").get(0));
    
    // ...
}

private Map<String, List<String>> parseQueryString(String data) {
    Map<String, List<String>> answer = new LinkedHashMap<String, List<String>>();
    for (String parameter : data.split("&")) {
        String[] entities = parameter.split("=");
        try {
            String key = URLDecoder.decode(entities[0], "utf-8");
            if (!answer.containsKey(key)) {
                answer.put(key, new ArrayList<String>());
            }

            answer.get(key).add(URLDecoder.decode(entities[1], "utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    return answer;
}
```

## HTTP ##
### Prerequisite ###
A request to establish a stream connection is GET request.

The only condition you have to meet is that the `Content-Type` header of the response must be set to `text/plain` that is the only one satisfying all streaming transport. `text/html` is also supported for easy integration with existing comet provider in 1.2, but not recommended for new project in general, because the Iframe transport can't help ignoring a first chunk of such a response.

If `enableXDR` option is set to true, the server-side application must set `Access-Control-Allow-Origin` header to either `*` or the exact URL of the requesting web page for the browser-based application to use XDomainRequest as a transport. The purpose of this header is to cause the response to be shared cross domain, but it cannot enable cross-domain comet because of obsolete browsers that can't support cors. For detail information about header, see [Cross-Origin Resource Sharing](http://www.w3.org/TR/access-control/). In 1.1, this setting was essential.

The server-side application doesn't need to configure anti-caching, since the timestamp parameter (`_`) is always included in the request URL in order to prevent caching.

**Example:** Meeting minimum prerequisites in Java
```
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    // Content-Type header
    response.setContentType("text/plain");

    // Access-Control-Allow-Origin header - optional
    // response.setHeader("Access-Control-Allow-Origin", "*");
    
    ...
}
```

### Default interaction ###
To do two-way communication over HTTP, the server-side application should meet the following requirements and rules. As of 1.2, you can redesign these interaction rules.

#### Id ####
For further communication, the stream id have to be generated and printed at the top of response. It must be unique within the server until the stream closes, so random number (or UUID) is ideal. It must end with a semicolon (;). The id and padding have to be transmitted to the client together. Generated id is managed and used by jQuery Stream.

#### Padding ####
The padding is needed by XMLHttpRequest of WebKit, XDomainRequest and Iframe transport. One kilobyte is usually enough. It have to end with a semicolon (;). If the browser receives response including id and padding, jQuery Stream handles it and fires open event.

**Example:** Printing id and padding in Java
```
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    ...

    PrintWriter writer = response.getWriter();

    // Id
    String id = UUID.randomUUID().toString();
    writer.print(id);
    writer.print(';');

    // Padding
    for (int i = 0; i < 1024; i++) {
        writer.print(' ');
    }
    writer.print(';');
    writer.flush();

    ...
}
```

#### Message ####
A message to be sent to client consists of size part and data part. The size part contains message's length and the data part contains message data itself. Each part must end with a semicolon (;). Every time a message arrived, jQuery Stream handles it and fires message event.

**Example:** Method to send message in Java
```
private void sendMessage(PrintWriter writer, String message) throws IOException {
    writer.print(message.length());
    writer.print(";");
    writer.print(message);
    writer.print(";");
    writer.flush();
}
```

#### Handling request ####
jQuery Stream use a POST request including metadata as request parameter to communicate with the server. The server-side application should identify the stream using request parameter `metadata.id` and should handle that request properly according to request parameter `metadata.type`. The following types of requests will be sent.

  * **send** - A message sent by the browser-based application.
  * **close** - A notification that browser closed the stream, therefore the server have to finish the corresponding stream connection to avoid the waste of server resources.

**Example:** Handling request in Java
```
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    request.setCharacterEncoding("utf-8");
    
    // AsyncContext is the execution context for an asynchronous operation
    AsyncContext ac = asyncContexts.get(request.getParameter("metadata.id"));
    if (ac == null) {
        return;
    }

    // close
    if ("close".equals(request.getParameter("metadata.type"))) {
        ac.complete();
        return;
    }

    // send - default case
    sendMessage(ac.getResponse().getWriter(), request.getParameter("message"));
}
```