package client;

import java.net.URI;

import org.java_websocket.drafts.Draft_17;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhaidaosi.game.jgframework.message.InMessage;
import com.zhaidaosi.game.jgframework.rsync.RsyncManager;
import com.zhaidaosi.game.jgframework.session.SessionManager;

import model.AuthResult;
import model.MyWebSocketClient;

public class TestMaxUser {
	private static final Logger log = LoggerFactory.getLogger(RsyncManager.class);
	private final URI uri;
	private String sercret;

	public TestMaxUser(URI uri, String sercret) {
		this.uri = uri;
		this.sercret = sercret;
	}

	public void run() throws Exception {
		long startTime = System.currentTimeMillis();
		MyWebSocketClient ch = new MyWebSocketClient(uri, new Draft_17());
		if (ch.connectBlocking()) {
			InMessage msg = new InMessage("test.test");
			msg.putMember(SessionManager.SECRET, sercret);
			ch.send(msg.toString());

			log.info("response:" + ch.getMessage());

			msg = new InMessage("test.test");
			msg.putMember("msg", "test");
			for (int i = 0; i < 10; i++) {
				Thread.sleep(5000);
				ch.send(msg.toString());
			}

			ch.closeBlocking();
		}
		long endTime = System.currentTimeMillis();
		log.info("end time : " + endTime + " | run time :" + (endTime - startTime));
	}

	public static void main(String[] args) throws Exception {
		for (int i = 1; i <= 60; i++) {
			MaxUserThread t = new MaxUserThread("test" + i, "123456");
			t.start();
			Thread.sleep(10);
		}
	}
}

class MaxUserThread extends Thread {
	private Logger log = LoggerFactory.getLogger(RsyncManager.class);

	String username;
	String password;

	public MaxUserThread(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public void run() {
		try {
			AuthResult ar = TestAuth.auth(username, password);
			if (ar != null) {
				log.info("ar.address : " + ar.address + "\t" + ar.sercret);
				new TestMaxUser(new URI(ar.address), ar.sercret).run();
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
		}
	}
}