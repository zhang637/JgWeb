package com.zhaidaosi.game.server.handler;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhaidaosi.game.jgframework.connector.IBaseConnector;
import com.zhaidaosi.game.jgframework.handler.BaseHandler;
import com.zhaidaosi.game.jgframework.message.IBaseMessage;
import com.zhaidaosi.game.jgframework.message.InMessage;
import com.zhaidaosi.game.jgframework.message.OutMessage;
import com.zhaidaosi.game.jgframework.model.area.IBaseArea;
import com.zhaidaosi.game.server.model.player.Player;

import io.netty.channel.Channel;

public class SendMsgHandler extends BaseHandler {
	private static final Logger log = LoggerFactory.getLogger(SendMsgHandler.class);

	@Override
	public IBaseMessage run(InMessage im, Channel ch) {
		Object msg = im.getMember("msg");
		if (msg == null || msg.equals("")) {
			return OutMessage.showError("msg 不能为空");
		}
		log.info(msg + "");

		Player player = (Player) ch.attr(IBaseConnector.PLAYER).get();
		IBaseArea area = player.gArea();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("msg", msg);
		map.put("player", player);
		area.getChannelGroup().write(OutMessage.showSucc(map, this.handlerName));
		return null;
	}
}
