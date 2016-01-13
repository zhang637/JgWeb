package com.zhaidaosi.game.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhaidaosi.game.jgframework.handler.BaseHandler;
import com.zhaidaosi.game.jgframework.message.IBaseMessage;
import com.zhaidaosi.game.jgframework.message.InMessage;
import com.zhaidaosi.game.jgframework.message.OutMessage;
import com.zhaidaosi.game.jgframework.model.area.AreaManager;
import com.zhaidaosi.game.server.model.area.Area;

import io.netty.channel.Channel;

public class OnlineUserHandler extends BaseHandler {
	private static final Logger log = LoggerFactory.getLogger(OnlineUserHandler.class);

	@Override
	public IBaseMessage run(InMessage im, Channel ch) {
		Area area = (Area) AreaManager.getArea(Area.ID);
		log.info(area.getName());
		return OutMessage.showSucc(area.getPlayers());
	}
}
