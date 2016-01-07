package com.zhaidaosi.game.server.handler.auth;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhaidaosi.game.jgframework.Boot;
import com.zhaidaosi.game.jgframework.common.BaseString;
import com.zhaidaosi.game.jgframework.common.encrpt.BaseMd5;
import com.zhaidaosi.game.jgframework.common.spring.ServiceManager;
import com.zhaidaosi.game.jgframework.handler.BaseHandler;
import com.zhaidaosi.game.jgframework.message.IBaseMessage;
import com.zhaidaosi.game.jgframework.message.InMessage;
import com.zhaidaosi.game.jgframework.message.OutMessage;
import com.zhaidaosi.game.jgframework.rsync.RsyncManager;
import com.zhaidaosi.game.jgframework.session.SessionManager;
import com.zhaidaosi.game.server.rsync.UserRsync;
import com.zhaidaosi.game.server.sdm.model.User;
import com.zhaidaosi.game.server.sdm.service.UserService;

import io.netty.channel.Channel;

public class LoginHandler extends BaseHandler {
	private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);
	UserService userService = (UserService) ServiceManager.getService(UserService.BEAN_ID);

	@Override
	public IBaseMessage run(InMessage im, Channel ch) throws Exception {
		HashMap<String, Object> args = im.getP();
		String username = (String) args.get("username");
		String password = (String) args.get("password");
		if (!BaseString.isEmpty(username) && !BaseString.isEmpty(password)) {
			User user = userService.findByUserName(username);
			if (user == null) {
				OutMessage showError = OutMessage.showError("该用户不存在", 10);
				log.info(showError.toString());
				return showError;
			}
			String checkPassword = BaseMd5.encrypt(username + password);
			if (checkPassword.equals(user.getPassword())) {
				int userId = user.getId();
				// 创建session
				String secret = SessionManager.createSecret(userId);
				HashMap<String, Object> result = new HashMap<String, Object>();
				result.put("address", Boot.getServiceAddress(SessionManager.getServerIp(userId)));
				result.put("secret", secret);
				user.setLastLoginTime(System.currentTimeMillis());
				// 同步用户状态
				RsyncManager.add(userId, UserRsync.class, user);
				// 返回状态消息
				return OutMessage.showSucc(result);
			}
		}
		return OutMessage.showError("账号密码错误", 11);
	}

}
