package client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhaidaosi.game.jgframework.common.BaseSocket;
import com.zhaidaosi.game.jgframework.message.InMessage;
import com.zhaidaosi.game.jgframework.message.OutMessage;
import com.zhaidaosi.game.jgframework.session.SessionManager;

import model.AuthResult;

class TestSocket {
	public static void main(String[] args) {
		for (int i = 0; i < 1; i++) {
			MyThread t = new MyThread("test" + i, "123456");
			t.start();
		}
	}
}

class MyThread extends Thread {
	private Logger log = LoggerFactory.getLogger(MyThread.class);

	String username;
	String password;

	public MyThread(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public void run() {
		try {
			long startTime = System.currentTimeMillis();
			AuthResult ar = TestAuth.auth(username, password);
			if (ar != null) {
				InMessage msg = new InMessage("init");
				msg.putMember(SessionManager.SECRET, ar.sercret);
				// URI uri = new URI(ar.address);
				// BaseSocket serverSocket =
				// BaseSocket.getNewInstance(uri.getHost(), uri.getPort());
				BaseSocket serverSocket = BaseSocket.getNewInstance("127.0.0.1", 28080);
				OutMessage request = serverSocket.request(msg);
				log.info(request.getH() + "-->" + request.getResult());

				msg = new InMessage("test.test");
				msg.putMember("msg", "test");
				for (int i = 0; i < 1000; i++) {
					OutMessage request2 = serverSocket.request(msg);
					log.info(request2.getH() + "--->" + request2.getResult());
					for (int j = 0; j < 10; j++) {
						serverSocket.heartBeat();
						// Thread.sleep(10);
						log.info("send heartBeat...");
					}
				}
				msg = new InMessage("onlineuser");
				msg.putMember("msg", "test");
				OutMessage request2 = serverSocket.request(msg);
				log.info(request2.getH() + "--->" + request2.getResult());
				serverSocket.close();
			}
			long endTime = System.currentTimeMillis();
			log.info("end time : " + endTime + " | run time :" + (endTime - startTime));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
