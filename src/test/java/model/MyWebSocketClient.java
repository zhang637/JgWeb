package model;

import java.net.URI;
import java.nio.channels.NotYetConnectedException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyWebSocketClient extends WebSocketClient {
	private static final Logger log = LoggerFactory.getLogger(MyWebSocketClient.class);

	private boolean received = false;
	private String message;

	public MyWebSocketClient(URI serverUri, Draft draft) {
		super(serverUri, draft);
	}

	public MyWebSocketClient(URI serverURI) {
		super(serverURI);
	}

	public String getMessage() {
		return message;
	}

	@Override
	public void send(String text) throws NotYetConnectedException {
		synchronized (this) {
			super.send(text);
			while (!received) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			received = false;
		}
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		log.info("--opened connection--");
		// if you plan to refuse connection based on ip or httpfields overload:
		// onWebsocketHandshakeReceivedAsClient
	}

	@Override
	public void onMessage(String message) {
		log.info("onMessage::::" + message + "-------------");
		this.message = message;
		synchronized (this) {
			received = true;
			this.notify();
		}
	}

	public void onFragment(Framedata fragment) {
		log.info("received fragment: " + new String(fragment.getPayloadData().array()));
		synchronized (this) {
			received = true;
			this.notify();
		}
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		// The codecodes are documented in class
		// org.java_websocket.framing.CloseFrame
		log.info("Connection closed by " + (remote ? "remote peer" : "us"));
		synchronized (this) {
			received = true;
			this.notify();
		}
	}

	@Override
	public void onError(Exception ex) {
		ex.printStackTrace();
		// if the error is fatal then onClose will be called additionally
		synchronized (this) {
			received = true;
			this.notify();
		}
	}
}