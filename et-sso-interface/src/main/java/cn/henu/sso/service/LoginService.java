package cn.henu.sso.service;

import cn.henu.common.utils.EtResult;

public interface LoginService {

	//参数用户名和密码,返回值为EtResult
	
	//业务逻辑
	/**
	 *1.判断用户名和密码是否正确,如果不正确返回登录页面
	 *2.正确生成token 
	 *3.把用户信息写入redis，key为token，value为用户信息
	 *4.设置session过期时间
	 *5.把token返回 EtResult,包含token信息
	 */
	EtResult userLogin(String username ,String password);
}
