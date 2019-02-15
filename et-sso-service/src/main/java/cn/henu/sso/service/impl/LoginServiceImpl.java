package cn.henu.sso.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import cn.henu.common.jedis.JedisClientCluster;
import cn.henu.common.utils.EtResult;
import cn.henu.common.utils.JsonUtils;
import cn.henu.mapper.TbUserMapper;
import cn.henu.pojo.TbUser;
import cn.henu.pojo.TbUserExample;
import cn.henu.pojo.TbUserExample.Criteria;
import cn.henu.sso.service.LoginService;

/**
 * 用户登录
 * @author syw
 *
 */
@Service
public class LoginServiceImpl implements LoginService {

	@Autowired
	private TbUserMapper tbUserMapper;
	@Autowired
	private JedisClientCluster jedisClientCluster;
	@Value("${SESSION_EXPIRE}")
	private Integer SESSION_EXPIRE;
	@Override
	public EtResult userLogin(String username, String password) {
		 //1.判断用户名和密码是否正确,如果不正确返回登录页面
		 //根据用户名查询用户信息
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		//执行查询
		List<TbUser> list = tbUserMapper.selectByExample(example);
		if(list==null||list.size()==0) {
			//返回登录失败
			return EtResult.build(400,"用户名或密码错误");
		}
		//取出用户信息
		TbUser user = list.get(0);
		//判断密码是否正确
		if(!DigestUtils.md5DigestAsHex(password.getBytes()).equals(user.getPassword())) {
			//如果不对
			return EtResult.build(400,"用户名或密码错误");
		}
		 //2.正确生成token
		String token = UUID.randomUUID().toString();
		 //3.把用户信息写入redis，key为token，value为用户信息
		 //注意这里要把密码清除了，不要带到客户端
		user.setPassword(null);
		jedisClientCluster.set("SESSION:"+token, JsonUtils.objectToJson(user));
		 //4.设置session过期时间
		jedisClientCluster.expire("SESSION:"+token, SESSION_EXPIRE);
		 //5.把token返回 EtResult,包含token信息
		 
		return EtResult.ok(token);
	}

}
