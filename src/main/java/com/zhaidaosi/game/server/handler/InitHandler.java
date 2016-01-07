package com.zhaidaosi.game.server.handler;

import com.zhaidaosi.game.jgframework.common.spring.ServiceManager;
import com.zhaidaosi.game.jgframework.connector.IBaseConnector;
import com.zhaidaosi.game.jgframework.handler.BaseHandler;
import com.zhaidaosi.game.jgframework.message.IBaseMessage;
import com.zhaidaosi.game.jgframework.message.InMessage;
import com.zhaidaosi.game.jgframework.message.OutMessage;
import com.zhaidaosi.game.server.model.player.Player;
import com.zhaidaosi.game.server.sdm.model.UserInfo;
import com.zhaidaosi.game.server.sdm.service.UserInfoService;

import io.netty.channel.Channel;

public class InitHandler extends BaseHandler {

	UserInfoService userInfoService = (UserInfoService) ServiceManager.getService(UserInfoService.BEAN_ID);

	@Override
	public IBaseMessage run(InMessage im, Channel ch) {

		Player player = (Player) ch.attr(IBaseConnector.PLAYER).get();

		UserInfo userInfo = (UserInfo) userInfoService.findByUid(player.getId());

		if (userInfo == null) {
			OutMessage.showError("初始化失败", 50001);
		}

		player.init(userInfo);

		return OutMessage.showSucc(player);
	}

}
