package cn.henu.sso.service;

import cn.henu.common.utils.EtResult;

/**
 * 根据token查询用户信息
 * @author syw
 *
 */
public interface TokenService {

	EtResult getUserBytoken(String token);
	
}
