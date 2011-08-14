package flowersinthesand.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.util.UrlEncoded;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

import com.google.gson.Gson;

public class ChatServlet extends WebSocketServlet {

	private static final long serialVersionUID = 4805728426990609124L;

	private Map<String, AsyncContext> asyncContexts = new ConcurrentHashMap<String, AsyncContext>();
	private Queue<ChatWebSocket> webSockets = new ConcurrentLinkedQueue<ChatWebSocket>();
	private BlockingQueue<String> messages = new LinkedBlockingQueue<String>();
	private Thread notifier = new Thread(new Runnable() {
		public void run() {
			boolean done = false;
			while (!done) {
				try {
					String message = messages.take();
					for (AsyncContext asyncContext : asyncContexts.values()) {
						try {
							sendMessage(asyncContext.getResponse().getWriter(), message);
						} catch (Exception e) {
							asyncContexts.values().remove(asyncContext);
						}
					}
					for (ChatWebSocket webSocket : webSockets) {
						try {
							webSocket.connection.sendMessage(message);
						} catch (Exception e) {
							webSockets.remove(webSocket);
						}
					}
				} catch (InterruptedException e) {
					done = true;
				}
			}
		}
	});

	private void sendMessage(PrintWriter writer, String message) throws IOException {
		writer.print(message.length());
		writer.print(";");
		writer.print(message);
		writer.print(";");
		writer.flush();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		notifier.start();
	}

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

		Map<String, String> data = new LinkedHashMap<String, String>();
		data.put("username", request.getParameter("username"));
		data.put("message", request.getParameter("message"));

		try {
			messages.put(new Gson().toJson(data));
		} catch (InterruptedException e) {
			throw new IOException(e);
		}
	}

	@Override
	public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol) {
		return new ChatWebSocket();
	}

	class ChatWebSocket implements WebSocket.OnTextMessage {

		Connection connection;

		@Override
		public void onOpen(Connection connection) {
			this.connection = connection;
			webSockets.add(this);
		}

		@Override
		public void onClose(int closeCode, String message) {
			webSockets.remove(this);
		}

		@Override
		public void onMessage(String queryString) {
			UrlEncoded parameters = new UrlEncoded(queryString);

			Map<String, String> data = new LinkedHashMap<String, String>();
			data.put("username", parameters.getString("username"));
			data.put("message", parameters.getString("message"));

			try {
				messages.put(new Gson().toJson(data));
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

	}

	@Override
	public void destroy() {
		messages.clear();
		webSockets.clear();
		asyncContexts.clear();
		notifier.interrupt();
	}

}
