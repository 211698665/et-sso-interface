package cn.henu.sso.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.utils.StringUtils;

import cn.henu.common.jedis.JedisClientCluster;
import cn.henu.common.utils.EtResult;
import cn.henu.common.utils.JsonUtils;
import cn.henu.pojo.TbUser;
import cn.henu.sso.service.TokenService;
/**
 * 根据token取用户信息
 * @author syw
 *
 */
@Service
public class TokenServiceImpl implements TokenService {

	@Autowired
	private JedisClientCluster jedisClientCluster;//这里使用的是集群版
	@Value("${SESSION_EXPIRE}")
	private Integer SESSION_EXPIRE;
	@Override
	public EtResult getUserBytoken(String token) {
		// 根据token去redis中取出用户信息
		String json = jedisClientCluster.get("SESSION:"+token);
		if(StringUtils.isBlank(json)) {
			//如果取不到信息，返回用户登录过期
			return EtResult.build(201, "用户登录过期");
		}
		//如果取到信息,更新token的过期时间
		jedisClientCluster.expire("SESSION:"+token, SESSION_EXPIRE);
		//返回一个EtResult对象，其中包含TbUser
		TbUser tbUser = JsonUtils.jsonToPojo(json, TbUser.class);
		return EtResult.ok(tbUser);
	}

}
