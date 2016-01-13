package client;

import java.net.URI;

import org.java_websocket.drafts.Draft_17;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhaidaosi.game.jgframework.message.InMessage;
import com.zhaidaosi.game.jgframework.session.SessionManager;

import model.AuthResult;
import model.MyWebSocketClient;

public class TestWebSocket {
	private static final Logger log = LoggerFactory.getLogger(TestWebSocket.class);

	private final URI uri;

	private String sercret;

	public TestWebSocket(URI uri, String sercret) {
		this.uri = uri;
		this.sercret = sercret;
	}

	public void run() throws Exception {
		long startTime = System.currentTimeMillis();
		MyWebSocketClient ch = new MyWebSocketClient(uri, new Draft_17());
		if (ch.connectBlocking()) {
			InMessage msg = new InMessage("init");
			msg.putMember(SessionManager.SECRET, sercret);
			ch.send(msg.toString());
			log.info("ch.getmessage:::" + ch.getMessage());

			msg = new InMessage("test.test");
			msg.putMember("msg", "test");
			ch.send(msg.toString());

			ch.closeBlocking();
		}
		long endTime = System.currentTimeMillis();
		log.info("end time : " + endTime + " | run time :" + (endTime - startTime));
	}

	public static void main(String[] args) throws Exception {
		for (int i = 0; i < 1; i++) {
			WebSocketThread t = new WebSocketThread("test" + i, "123456");
			t.start();
			Thread.sleep(10);
		}
	}
}

class WebSocketThread extends Thread {
	private static final Logger log = LoggerFactory.getLogger(WebSocketThread.class);

	String username;
	String password;

	public WebSocketThread(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public void run() {
		try {
			AuthResult ar = TestAuth.auth(username, password);
			if (ar != null) {
				new TestWebSocket(new URI(ar.address), ar.sercret).run();
			}
			log.info("ar is :" + ar);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
