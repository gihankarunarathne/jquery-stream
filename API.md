# API Reference #


## jQuery.stream ##
The facade of Stream.

The mapping between the jQuery version and the jQuery Stream version is:
| **jQuery Stream version** | **jQuery version** |
|:--------------------------|:-------------------|
| 1.2 | 1.5|
| 1.1 | 1.4|
| 1.0 | 1.4|

<h3><a href='http://code.google.com/p/jquery-stream/issues/list?can=1&q=label%3ATarget-1.2'>Version 1.2</a></h3>

1.2 focuses on the integration with the existing server-side framework providing comet functionality and provides the low-level handlers that can directly deal with the interaction with the server.
  * response handler - handleOpen and handleMessage
  * request handler - handleSend
  * text/html support
  * operaInterval and iframeInterval option

<h3><a href='http://code.google.com/p/jquery-stream/issues/list?can=1&q=label%3ATarget-1.1.1'>Version 1.1.1</a></h3>

Iframe transport replaces XDomainRequest transport to maintain user's session.
  * jQuery.stream.setup()
  * global option
  * openData option
  * enableXDR and rewriteURL option

<h3><a href='http://code.google.com/p/jquery-stream/issues/list?can=1&q=label%3ATarget-1.1'>Version 1.1</a></h3>

When migrating from 1.0, you must add the `type` option having `http` as a value to stream options.
  * WebSocket support
  * alias option

<h3>Version 1.0</h3>
  * HTTP Streaming support

### jQuery.stream() ###
`Stream jQuery.stream()`

Returns the first Stream in the document.

### jQuery.stream(url) ###
`Stream jQuery.stream(String url)`

Returns the Stream to which the specified url or alias is mapped.

### jQuery.stream(url, options) ###
`Stream jQuery.stream(String url, Map options)`

Creates a Stream connection (a long-held HTTP request or a WebSocket connection) allowing a server to push data to a client only if the Stream to which the given url is mapped doesn't exist or does exist and is closed.

**url** - Specifies the URL to which to connect. If it starts with WebSocket protocol such as ws: or wss:, the stream simply behaves as a wrapper for WebSocket. If it starts with HTTP protocol such as http: or https:, the stream performs HTTP streaming.

**options** - Configures the Stream connection.

> <b>alias</b> <font size='1'>(added in 1.1)</font> - An alias of the url. When retrieving the stream instance by the url, you can use it instead of the lengthy url.

> <b>close</b> - A function or an array of functions to be called if the stream closes. Each function receives a event named `close` which is based on `jQuery.Event` and `CloseEvent` interface and the stream instance as the arguments.

> <b>context</b> - The value to be passed as `this` to the callback when the event is triggered.

> <b>converters</b> - A map of data-type/data-converter pairs. data-converter takes one argument which is a raw data and returns a transformed data. The default is `{text: window.String, json: $.parseJSON, xml: $.parseXML`}, `jQuery.parseJSON` is in 1.4.1 and `jQuery.parseXML` is in 1.5.

> <b>dataType</b> - The type of data that a server push to a client. The handled data is passed as the `data` attribute of message event. The supported types are `text`, `json` and `xml`. The default is `text` itself.

> <b>enableXDR</b> <font size='1'>(added in 1.1.1)</font> - If set to `true`, XDomainRequest will be used as a transport when available.

> <b>error</b> - A function or an array of functions to be called if an error is detected. Each function receives a simple event named `error` which is based on `jQuery.Event` and the stream instance as the arguments.

> <b>global</b> <font size='1'>(added in 1.1.1)</font> - Whether to trigger global Stream event handlers for this stream. The default is `true`.

> <b>handleMessage</b> <font size='1'>(added in 1.2)</font> - A response handler for HTTP Streaming that can be executed whenever the transport receives a response from the server after it is open. The handler receives the following as parameters: the response text sent from the server; a helper object for parsing response; the stream. The handler must set a `index` property of the second argument to a last index of parsed text and set a `data` property to parsed text. If the handler doesn't return `false`, the stream will fire a message event regarding a value of the `data` property of the helper object for parsing response as the data of the event.

> <b>handleOpen</b> <font size='1'>(added in 1.2)</font> - A response handler for HTTP Streaming that can be executed whenever the transport receives a response from the server before it is open. The handler gets passed three arguments: the response text sent from the server - empty string means that the transport is iframe and the response is html; a helper object for parsing response; the stream. If the handler returns anything but `false`, a open event will be fired so that the handler will not be executed again.

> <b>handleSend</b> <font size='1'>(added in 1.2)</font> - A request handler for HTTP Streaming to be called before the stream communicate with the server. The handler gets passed three arguments: the type of the request; the `jQuery.ajax` options; the stream. Returning `false` cancels that request.

> <b>iframeInterval</b> <font size='1'>(added in 1.2)</font> - A polling interval for HTTP Streaming in Internet Explorer. The stream will check changes in the response per the interval in milliseconds, and it works only with iframe transport. The default is `0`, but the effective value varies with browser version.

> <b>message</b> - A function or an array of functions to be called if the stream receives a data. Each function receives a event named `message` which is based on `jQuery.Event` and `MessageEvent` interface and the stream instance as the arguments. The event has the data attribute which is sent by the server and is converted according to `dataType`.

> <b>open</b> - A function or an array of functions to be called if the stream opens. Each function receives a simple event named `open` which is based on `jQuery.Event` and the stream instance as the arguments.

> <b>openData</b> <font size='1'>(added in 1.1.1)</font> - Additional parameters for open request. It is serialized to a query string and appended to the url.

> <b>operaInterval</b> <font size='1'>(added in 1.2)</font> - A polling interval for HTTP Streaming in Opera. The stream will check changes in the response per the interval in milliseconds. The default is `0`, but the effective value varies with browser version.

> <b>protocols</b> <font size='1'>(added in 1.1)</font> - An argument to be used to create WebSocket instance.

> <b>reconnect</b> - Whether to automatically reconnect when connection closed. This is set to `true` by default for continuous stream.

> <b>rewriteURL</b> <font size='1'>(added in 1.1.1)</font> - A function that can be used to modify url to contain a current user's session identifier when XDomainRequest is used as a transport. This is only effective when `enableXDR` options is true and XDomainRequest is available. It receives the original url and must return the modified url. The default function supports Java and PHP. For example, If a url is `echo?foo=bar` and JSESSIONID cookie exists, the url becomes `echo;jsessionid=${JSESSIONID}?foo=bar`, and if PHPSESSID cookie exists, the url becomes `echo?PHPSESSID=${PHPSESSID}&foo=bar`.

> <b>throbber</b> - Only for Webkit-based browsers such as Chrome and Safari. This option is to remove 'Throbber of doom' which is spinning loading indicator to show the user that a request is performing. The throbber doesn't be removed only when a Stream is created before all the requests such as image, script, iframe and ajax have finished loading, because a streaming connection is HTTP persistent connection. The following strategies are available:
> > lazy - After the window's load event fires and some `delay`, it opens a Stream connection. The default setting for lazy is `{type: "lazy", delay: 50`} or simply `"lazy"`. It is suitable for typical use, unless the page contains many resources so the time taken for loading is very long. This is default.<br />
> > reconnect - Regardless of the window's load event, it opens a Stream connection immediately and closes it when the window's load event fires. Likewise after some `delay`, it opens a Stream connection again. The default setting for reconnect is `{type: "reconnect", delay: 50`} or simply `"reconnect"`. The request which opens a Stream is used twice, but you can provide seamless Stream for the user navigating pages.


> Since both of the above strategy use the window's load event handler, a logic creating a Stream (i.e `jQuery.stream("/stream", {})`) must not be located there in order to work this properly.
    * iOS Safari and Android Chrome are currently unsupported.
    * The load event of window doesn't consider ajax request, so when the event fires, incompleted Ajax request can get the throbber to keep spinning.

> <b>type</b> <font size='1'>(added in 1.1)</font> - The type of stream. The default is `ws` when WebSocket is available, `http` otherwise. If a scheme is specified in the url, the normalized scheme will be type value, ignoring a user-defined value.

<h3>Error and close event</h3>

Currently, the role of `error` event is ambiguous according to latest [W3C editor's draft](http://dev.w3.org/html5/websockets/) and [W3C working draft](http://www.w3.org/TR/websockets/) of The WebSocket API specification. For the moment, I recommend `error` event handler set to the same handler of `close` event because they mean that whatever the reason the stream is closed.

**Example:** Stream wrapping a WebSocket instance. If browser doesn't implement WebSocket, nothing happens.
```
// Absolute path
$.stream("ws://example.com/stream", {});

// Relative path
$.stream("/stream", {type: "ws"});
```

**Example:** Stream performing HTTP Streaming.
```
// Absolute path
$.stream("http://example.com/stream", {});

// Relative path
$.stream("/stream", {type: "http"});
```

**Example:** The browser supporting WebSocket will create WebSocket instance whose url is `ws://example.com/stream`, and the others performs HTTP streaming on `http://example.com/stream`.
```
$.stream("/stream", {});
```

**Example:** The browser supporting WebSocket will create WebSocket instance whose url is `ws://example.com/stream-websocket` and protocols is `chat`, and the others performs HTTP streaming on `http://example.com/stream-http`.
```
$.stream(window.WebSocket ? "/stream-websocket" : "/stream-http", {
    alias: "stream",
    protocols: "chat"
});
```

**Example:** Retrieving a stream.
```
$.stream("http://example.com/event-stream", {alias: "event", dataType: "json"});

// If that stream is the first stream in the document
$("#message-text").keyup(function(event) {
    if (event.which === 13) {
        $.stream().send({message: this.value});
    }
});

// Using url
$("#notice-button").click(function() {
    $.stream("http://example.com/event-stream").send({ts: new Date().getTime()});
    return false;
});

// Using alias
$("#close").click(function() {
    $.stream("event").close();
});
```

**Example:** Stream dispatching server-sent events into jQuery's event system.
```
$.stream("/event-stream", {
    dataType: "json",
    message: function(event) {
        $.event.trigger(event.data.name, event.data.data);
    }
});
```

**Example:** Adding custom converter
```
$.stream("/csv-stream", {
    dataType: "csv",
    converters: {
        "csv" : function(data) {
            return data.split(",");
        }
    }
});
```

**Example:** Introducing `this` to the callbacks.
```
$.stream("/notice", {
    dataType: "xml",
    context: $("#notice")[0],
    message: function(event, stream) {
        $("<p class='message'>" + event.data.message + "</p>").prependTo(this);
    }
});
```

**Example:** Disposable stream.
```
$.stream("/countdown.jsp", {
    reconnect: false,
    message: function(event, stream) {
        $("<p>" + event.data + " from " + stream.url + "</p>").appendTo("#panel");
    }
});
```

**Example:** Stopping the throbber using reconnect strategy with 100ms delay.
```
$.stream("/test.action", {
    throbber: {type: "reconnect", delay: 100}
});
```

**Example:** Stream with all callbacks.
```
$.stream("/chat", {
    dataType: "json",
    context: $("#content")[0],
    open: function(event, stream) {
        $("#editor").removeAttr("disabled").focus();
        stream.send({username: chat.username, message: "Hello"});
    },
    message: function(event, stream) {
        $("<p />").addClass("message").text(event.data.message).appendTo(this);
    },
    error: function() {
        $("#editor").attr("disabled", "disabled");
    },
    close: function() {
        $("#editor").attr("disabled", "disabled");
    }
});
```

**Example:** Suppressing global Stream events.
```
$.stream("/event", {
    global: false,
    message: function(event) {
        alert(event.data);
    }
});
```

**Example:** Additional parameters for open request.
```
$.stream("/stream", {
    openData: {
        type: "json",
        value: function() {
            return $("#value").val();
        }
    }
});
```

**Example:** Enabling XDomainRequest transport
```
$.stream("/stream", {
    enableXDR: true,
    rewriteURL: function(url) {
        var sessionId = "${pageContext.session.id}";
        return sessionId ? url.replace(/;jsessionid=[^\?]*|(\?)|$/, ";jsessionid=" + sessionId + "$1") : url;
    }
});
```

**Example:** Integrating with existing stream
```
$.stream("/legacy-stream", {
    // If there is no id
    handleOpen: function(text, message, stream) {
        message.index = text.length;
    },
    // If the messages are separated by \r\n
    handleMessage: function(text, message) {
        var end = text.indexOf("\r\n", message.index);
        if (end < 0) {
        	return false;
        }
        
        message.data = text.substring(message.index, end);
        message.index = end + "\r\n".length;
    },
    // If there is no need to communicate with the server
    handleSend: function() {
        return false;
    }
    dataType: "json"
})
```

**Example:** Modifying request
```
$.stream("/stream", {
    // if you only need send-type request and don't need metadata
    handleSend: function(type, options, stream) {
        if (type !== "send") {
            return false;
        }
        
        // You can also attach Ajax event handler
        options.success = function() {
            alert("success!");
        }
        options.error = function() {
            alert("error!");
        }
    }
})
```

### jQuery.stream.version ###
`String jQuery.stream.version`

Contains the version number of jQuery Stream.

### jQuery.stream.setup(options) ###
`Object jQuery.stream.setup(options)`

Sets default options.

**Example:** Automatically enabling XDomainRequest
```
$.stream.setup({enableXDR: true});
```

## jQuery.fn ##
Global Stream event handlers.

### .streamOpen(handler(event, streamEvent, stream)) ###
`jQuery .streamOpen(Function handler)`

Registers a handler to be called when the Stream has opend.

**Example:** Sending a message when Stream opens.
```
$(document).streamOpen(function(e, event, stream) {
    stream.send("Hello");
});
```

**Example:** Focusing the text input box when Stream opens.
```
$("#form :text").streamOpen(function() {
    $(this).focus();
});
```

### .streamMessage(handler(event, streamEvent, stream)) ###
`jQuery .streamMessage(Function handler)`

Registers a handler to be called when the Stream has received a message.

**Example:** Showing a server-sent message whenever Stream receives a message.
```
$("#messages").streamMessage(function(e, event) {
    $("<p />").addClass("message").html(event.data.message).appendTo(this);
});
```

### .streamError(handler(event, streamEvent, stream)) ###
`jQuery .streamError(Function handler)`

Registers a handler to be called when the Stream has detected a error.

**Example:** Displaying an alert dialog when Stream detects a error.
```
$(document).streamError(function() {
    alert("stream error!");
});
```

### .streamClose(handler(event, streamEvent, stream)) ###
`jQuery .streamClose(Function handler)`

Registers a handler to be called when the Stream has closed.

**Example:** Hiding the text input box when Stream whose url is '/test' closes.
```
$("#form :button").streamClose(function(e, event, stream) {
    if (stream.url === "/test") {
        $(this).prop("disabled", true);
    }
});
```

## Stream Object ##
Stream Object is the main agent that provides Comet Streaming using HTTP and WebSocket protocol. It is based on [The WebSocket API](http://dev.w3.org/html5/websockets/) which is the ultimate solution to true two-way communication between a client and a server. The current target WebSocket API version - [W3C Working Draft 19 April 2011](http://www.w3.org/TR/2011/WD-websockets-20110419/).

It doesn't rely on any specific server-side technology and third-party plugin-in such as ActiveX and Flash, so can be used with any server-side technology supporting asynchronous processing as long as the server meets the requirements depending on the protocol. For details about the requirements, see ServerSideProcessing.

<h3>Two-way communication over WebSocket</h3>

Stream transparently wraps WebSocket instance.

If the stream receives a non-string data via `send` method, the stream converts it into a query string by using `jQuery.param` and transmits it to the server. In the future, this feature will be changed to consider binary data.

<h3>Two-way communication over HTTP</h3>

Stream performs HTTP Streaming by choosing proper transport according to browser and two-way communication by simulating full duplex socket under customizable interaction rules.

<h4>Transport</h4>

When a stream instance is created, the chosen transport according to the browser sends a GET request which will be a one-way socket receiving a data from a server in real-time and deals with changes in that request's response.

  * **XMLHttpRequest** - For modern browsers except Internet Explorer. but in the case of Opera, it polls the response text manually using `setTimeout`.

  * **Enhanced Iframe** - For Internet Explorer. It's different from the traditional way of fetching response. It polls the response text manually using `setTimeout` instead of requiring the server to print `script` tags. By doing this, you can simply print response and don't need to consider user agent. The element containing the response is emptied every time that the response fragment is handled, so there is no memory accumulation. If the content type of the response is `text/html`, the first chunk will be interpreted as HTML and its interpreted content will be unreliable, so empty string will be passed to `handleOpen`.

  * **XDomainRequest** - Optionally available in Internet Explorer 8+. See [COMET Streaming in Internet Explorer](http://blogs.msdn.com/b/ieinternals/archive/2010/04/06/comet-streaming-in-internet-explorer-with-xmlhttprequest-and-xdomainrequest.aspx) and [XDomainRequest Object (MSDN)](http://msdn.microsoft.com/en-us/library/cc288060%28VS.85%29.aspx). For security reasons, XDomainRequest excludes cookies when sending a request, so in most cases session state is not maintained. Therefore, as of jQuery Stream 1.1.1, Iframe replaces XDomainRequest. Since the performance of XDomainRequest, of course, is far better than Iframe, it is recommended to use XDomainRequest if possible. The previous problem can be solved by rewriting url to contain the session id. How to rewrite url is depending on the server-side technology: For details, see [my question and answer](http://stackoverflow.com/questions/6453779/maintaining-session-by-rewriting-url) on stackoverflow.

<h4>Interaction</h4>

Two-way communication is not possible without specific rules, because HTTP Streaming is one-way.

To enable it, jQuery Stream uses internally the id and the metadata by default. The id is the unique identifier of the stream within the server, and should be generated by the server at the top of the GET request's response. The metadata is a series of the request parameters starting with `metadata.` and included in request body to inform the server of additional data needed by server-side processing. It includes id, type and so on. The following list enumerates all kinds of request type.

  * **send** - Caused by calling `send` method.
  * **close** - Caused by calling `close` method, and notify the server because connection remains still open after aborting it in client-side.

As of 1.2, above concepts and response format can be completely reimplemented if needed. If you have to integrate with the specific comet framework or you can't change server-side implementation, this feature will be very helpful.

### .url ###
`String url`

The Stream URL.

### .options ###
`Map options`

The Stream options.

### .readyState ###
`Number readyState`

The state of the stream. It is the same as WebSocket's readyState in terms of meaning.

**Example:** Checking the state of the stream.
```
$("#stream-state").click(function() {
    switch ($.stream().readyState) {
    case 0:
        alert("CONNECTING");
        break;
    case 1:
        alert("OPEN");
        break;
    case 2:
        alert("CLOSING");
        break;
    case 3:
        alert("CLOSED");
        break;
    }
});
```

### .send(data) ###
`void .send(Object data)`

Transmits data through the stream.

**data** - The data to be sent to the server. If not already a string, it is converted to a query string by `jQuery.param`.

**Example:** Transmitting the mouse position.
```
$(document).click(function(event) {
    $.stream().send({x: event.pageX, y: event.pageY});
});
```

### .close() ###
`void .close()`

Disconnects the stream.

**Example:** Closing the stream.
```
$("#close-stream").click(function() {
    $.stream().close();
});
```