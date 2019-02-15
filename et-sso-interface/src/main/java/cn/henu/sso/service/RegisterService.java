package cn.henu.sso.service;

import cn.henu.common.utils.EtResult;
import cn.henu.pojo.TbUser;

public interface RegisterService {

	public EtResult checkData(String param,int type);
	public EtResult register(TbUser user);
}