package com.zhaidaosi.game.server.handler.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhaidaosi.game.jgframework.handler.BaseHandler;
import com.zhaidaosi.game.jgframework.message.IBaseMessage;
import com.zhaidaosi.game.jgframework.message.InMessage;
import com.zhaidaosi.game.jgframework.message.OutMessage;

import io.netty.channel.Channel;

public class TestHandler extends BaseHandler {
	private static final Logger log = LoggerFactory.getLogger(TestHandler.class);

	@Override
	public IBaseMessage run(InMessage im, Channel ch) {
		Object msg = im.getMember("msg");
		log.info("" + msg);
		if (msg != null) {
			return OutMessage.showSucc("system : " + msg);
		} else {
			return OutMessage.showSucc(im.toString());
		}
	}

}
