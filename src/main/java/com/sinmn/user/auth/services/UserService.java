package com.sinmn.user.auth.services;

import com.sinmn.core.model.emun.ModelOperator;
import com.sinmn.core.utils.exception.CommonException;
import com.sinmn.core.utils.util.BeanUtil;
import com.sinmn.core.utils.util.IPUtil;
import com.sinmn.core.utils.util.LongUtil;
import com.sinmn.core.utils.util.StringUtil;
import com.sinmn.core.utils.verify.VerifyUtil;
import com.sinmn.mjar.ext.core.UserExtCore;
import com.sinmn.user.auth.constant.AppAuthConstant;
import com.sinmn.user.auth.model.User;
import com.sinmn.user.auth.model.UserLoginLog;
import com.sinmn.user.auth.redis.UserRedisDao;
import com.sinmn.user.auth.repository.UserLoginLogRepository;
import com.sinmn.user.auth.repository.UserRepository;
import com.sinmn.user.auth.util.PasswdUtil;
import com.sinmn.user.auth.vo.inVO.LoginInVO;
import com.sinmn.user.auth.vo.inVO.RepasswdInVO;
import com.sinmn.user.auth.vo.innerVO.UserInfoInnerVO;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Log4j
@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserLoginLogRepository userLoginLogRepository;

	@Autowired
	private UserExtCore userExtCore;

	@Autowired
	private UserRedisDao userRedisDao;

	private static String targetIp;

	static {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			targetIp = addr.getHostAddress().toString(); //获取本机ip
		} catch (Exception e) {
		}
	}




	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object login(LoginInVO loginInVO, HttpServletRequest req) throws CommonException{

		//验证
		VerifyUtil.verify(loginInVO);

		//登录成功,插入登录日志
		UserLoginLog userLoginLog = new UserLoginLog();

		User user = userRepository
				.where(User.ACCOUNT,loginInVO.getAccount())
				.or(User.EMAIL,loginInVO.getAccount())
				.get();

		if(user == null){
			throw new CommonException("账号错误");
		}

		if(user.getIsActive() == AppAuthConstant.Common.NO){
			throw new CommonException("账号已经被冻结，请联系管理员激活账号");
		}

		if(user.getTryCount() >= 6){
			throw new CommonException("密码输入错误次数过多，账号已经被封停，请联系系统管理人员解封");
		}

		userLoginLog.setCompanyId(user.getCompanyId());
		userLoginLog.setCreateTime(new Date());
		userLoginLog.setStatus(AppAuthConstant.Common.NO);
		userLoginLog.setTargetIp(targetIp);
		userLoginLog.setUserId(user.getId());
		userLoginLog.setIp(IPUtil.getIpAddr(req));
		userLoginLog.setAccount(loginInVO.getAccount());
		userLoginLog.setAddress(IPUtil.getIpRealAddr(userLoginLog.getIp()));

		if(!PasswdUtil.getPasswd(loginInVO.getPasswd(), user.getPasswdSuffix()).equals(user.getPasswd())){
            user.setTryCount(user.getTryCount() + 1);
			if(user.getTryCount() >= 6){
                user.setIsActive(AppAuthConstant.Common.NO);
			}
			userRepository.include(User.TRY_COUNT,User.IS_ACTIVE).update(user);
			String message = "";
			if(user.getTryCount() > 2){
				message = String.format("输入错误 %d 次,你还有 %d 次机会",user.getTryCount(), 6-user.getTryCount());
			}
			userLoginLog.setReason("密码错误:"+loginInVO.getPasswd());
			userLoginLogRepository.insert(userLoginLog);
			throw new CommonException("密码错误"+message);
		}
		user.setTryCount(0);
		userRepository.include(User.TRY_COUNT).update(user);

		String sessionKey = UUID.randomUUID().toString().replaceAll("-", "");

		UserInfoInnerVO userInfoInnerVO = new UserInfoInnerVO();

		userInfoInnerVO.setSessionKey(sessionKey);
		userInfoInnerVO.setUserId(user.getId());
		userInfoInnerVO.setCompanyId(user.getCompanyId());
		userInfoInnerVO.setUserName(user.getName());
		
		Map<String,Object> mapResult = new HashMap<String, Object>();
		
		//登陆
        //保存会话
        userExtCore.holdSeesion(sessionKey,userInfoInnerVO.toJsonString());
		mapResult.put("userId", user.getId());
		mapResult.put("userName", user.getName());
		mapResult.put("sessionKey", sessionKey);
		
		userLoginLog.setStatus(AppAuthConstant.Common.YES);
		userLoginLogRepository.insert(userLoginLog);
		
		
		return mapResult;
	}
	
	public Object save(User user, UserInfoInnerVO userInfoInnerVO) throws CommonException{
		VerifyUtil.verify(user);
		user.setCompanyId(userInfoInnerVO.getCompanyId());
		user.setIsAdmin(AppAuthConstant.Common.NO);
		if(LongUtil.isNotZero(user.getId())){
			User orgAuthUser = userRepository
				.where(User.COMPANY_ID,user.getCompanyId())
				.where(User.ID,user.getId()).get();
			if(orgAuthUser == null || orgAuthUser.getIsAdmin() == AppAuthConstant.Common.YES){
				throw new CommonException("无此用户权限");
			}
			user.setPasswd(null);
			if(userRepository
					.where(User.ORG_ACCOUNT,user.getOrgAccount())
					.where(User.COMPANY_ID,user.getCompanyId())
					.where(User.ID,user.getId(), ModelOperator.NEQ).isExists()){
				throw new CommonException("账号已经被占用");
			}
			if(userRepository
					.where(User.EMAIL,user.getEmail())
					.where(User.ID,user.getId(), ModelOperator.NEQ).isExists()){
				throw new CommonException("邮箱已经被占用");
			}
			if(userRepository
					.where(User.NAME,user.getName())
					.where(User.COMPANY_ID,user.getCompanyId())
					.where(User.ID,user.getId(), ModelOperator.NEQ).isExists()){
				throw new CommonException("名字已经被占用");
			}
			BeanUtil.initModify(user, userInfoInnerVO.getUserName());
		}else{
			if(userRepository
					.where(User.ORG_ACCOUNT,user.getOrgAccount())
					.where(User.COMPANY_ID,user.getCompanyId())
					.isExists()){
				throw new CommonException("账号已经被占用");
			}
			if(userRepository
					.where(User.EMAIL,user.getEmail())
					.isExists()){
				throw new CommonException("邮箱已经被占用");
			}
			if(userRepository
					.where(User.NAME,user.getName())
					.where(User.COMPANY_ID,user.getCompanyId())
					.isExists()){
				throw new CommonException("名字已经被占用");
			}
			BeanUtil.initCreate(user, userInfoInnerVO.getUserName());
		}
		
		if(StringUtil.isNotEmpty(user.getPasswd())){
			user.setPasswdSuffix(UUID.randomUUID().toString().replaceAll("-", ""));
			user.setPasswd(PasswdUtil.getPasswd(user.getPasswd(), user.getPasswdSuffix()));
		}

		
		user.setIsAdmin(AppAuthConstant.Common.NO);
		userRepository.save(user);
		
		return user;
	}
	
	public Object rePasswd(RepasswdInVO repasswdInVO, UserInfoInnerVO userInfoInnerVO) throws CommonException{
		//验证
		VerifyUtil.verify(repasswdInVO);
		
		User user = userRepository.get(userInfoInnerVO.getUserId());
		
		if(user.getTryCount() >= 6){
			throw new CommonException("密码输入错误次数过多，账号已经被封停，请联系系统管理人员解封");
		}
		
		if(user.getIsActive() == AppAuthConstant.Common.NO){
			throw new CommonException("账号已经被冻结，请联系管理员激活账号");
		}
		
		if(!user.getPasswd().equals(PasswdUtil.getPasswd(repasswdInVO.getPasswd(), user.getPasswdSuffix()))){
            user.setTryCount(user.getTryCount() + 1);
			userRepository.include(User.TRY_COUNT).update(user);
			String message = "";
			if(user.getTryCount() > 2){
				message = String.format("输入错误 %d 次,你还有 %d 次机会",user.getTryCount(), 6-user.getTryCount());
			}
			throw new CommonException("原始密码错误 "+message);
		}

        user.setPasswdSuffix(UUID.randomUUID().toString().replaceAll("-", ""));
        user.setPasswd(PasswdUtil.getPasswd(repasswdInVO.getNewPasswd(), user.getPasswdSuffix()));
        user.setTryCount(0);
		userRepository.include(User.TRY_COUNT,User.PASSWD,User.PASSWD_SUFFIX).update(user);
		return null;
	}

}
