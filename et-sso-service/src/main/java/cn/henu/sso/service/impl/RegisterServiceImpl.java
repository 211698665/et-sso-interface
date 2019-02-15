package cn.henu.sso.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import cn.henu.common.utils.EtResult;
import cn.henu.mapper.TbUserMapper;
import cn.henu.pojo.TbUser;
import cn.henu.pojo.TbUserExample;
import cn.henu.pojo.TbUserExample.Criteria;
import cn.henu.sso.service.RegisterService;

/**
 * 用户注册的处理
 * @author syw
 *
 */
@Service
public class RegisterServiceImpl implements RegisterService {

	@Autowired
	private TbUserMapper userMapper;
	@Override
	public EtResult checkData(String param, int type) {
		//根据不同的type生成不同的查询条件
		TbUserExample example = new TbUserExample();
		//执行查询
		Criteria criteria = example.createCriteria();
		//1.用户名,2.手机号,3.邮箱
		if(type==1) {
			criteria.andUsernameEqualTo(param);
		} else if(type==2) {
			criteria.andPhoneEqualTo(param);
		}else {
			return EtResult.ok(false);
		}
		//判断结果中是否包含数据
		List<TbUser> list = userMapper.selectByExample(example);
		//如果有数据返回false
		if(list!=null&&list.size()>0) {
			return EtResult.ok(false);
		}
		//没有数据返回true
		return EtResult.ok(true);
	}
	@Override
	public EtResult register(TbUser user) {
		//先校验数据的有效性StringUtils.isBlank()可以同时判定空和空串
		if(StringUtils.isBlank(user.getUsername())||StringUtils.isBlank(user.getPassword())||StringUtils.isBlank(user.getPhone())) {
			return EtResult.build(400, "用户数据不完整,注册失败");
		}
		//1.用户名，2.手机号，3.邮箱
		EtResult result = checkData(user.getUsername(),1);
		if(!(boolean) result.getData()) {
			return EtResult.build(400, "此用户名已经被占用");
		}
		EtResult result2 = checkData(user.getPhone(),2);
		if(!(boolean) result.getData()) {
			return EtResult.build(400, "此手机号已经被注册");
		}
		// 补全pojo
		user.setCreated(new Date());
		user.setUpdated(new Date());
		//对password进行MD5加密,只要使用spring就能直接拿到MD5
		String md5Pass = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
		user.setPassword(md5Pass);
		//插入到数据库
		int i = userMapper.insert(user);
		//返回添加成功
		EtResult etResult = new EtResult(user.getUsername());
		return etResult;
	}

}
