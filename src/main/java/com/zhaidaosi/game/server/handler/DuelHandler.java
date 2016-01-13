package com.zhaidaosi.game.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhaidaosi.game.jgframework.connector.IBaseConnector;
import com.zhaidaosi.game.jgframework.handler.BaseHandler;
import com.zhaidaosi.game.jgframework.message.IBaseMessage;
import com.zhaidaosi.game.jgframework.message.InMessage;
import com.zhaidaosi.game.jgframework.message.OutMessage;
import com.zhaidaosi.game.jgframework.session.SessionManager;
import com.zhaidaosi.game.server.model.Duel;
import com.zhaidaosi.game.server.model.player.Player;
import com.zhaidaosi.game.server.model.zone.DuelZone;

import io.netty.channel.Channel;

public class DuelHandler extends BaseHandler {
	private static final Logger log = LoggerFactory.getLogger(DuelHandler.class);

	@Override
	public IBaseMessage run(InMessage im, Channel ch) {
		Player me = (Player) ch.attr(IBaseConnector.PLAYER).get();
		log.info(me.getName());
		Integer targetId = 0;
		if (im.getMember("uid") != null) {
			targetId = Integer.valueOf((String) im.getMember("uid"));
		}
		if (targetId < 1) {
			return OutMessage.showError("目标用户id为空");
		}
		Player target = (Player) SessionManager.getPlayerByUserId(targetId);
		if (target == null) {
			return OutMessage.showError("目标用户已下线");
		}
		synchronized (target) {
			if (target.gArea().getName().endsWith(DuelZone.ZONE_NAME)) {
				return OutMessage.showError("目标用户正在打斗中...");
			}
			// new 一个打架场地
			DuelZone zone = new DuelZone();
			Player winer = Duel.doDuel(me, target, zone, this.getHandlerName());
			winer.addExperience(5);
		}
		return OutMessage.showSucc(me);
	}
}
