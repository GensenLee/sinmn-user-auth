package com.sinmn.user.auth.context;


import com.sinmn.user.auth.vo.innerVO.UserInfoInnerVO;

public class AppAuthContext {

	private static ThreadLocal<String> sessionKeyHandle = new ThreadLocal<String>();
	
	private static ThreadLocal<UserInfoInnerVO> userInfoInnerVOHandle = new ThreadLocal<UserInfoInnerVO>();
	
	public static String getSessionKey() {
		return sessionKeyHandle.get();
	}

	public static void setSessionKey(String sessionKey) {
		AppAuthContext.sessionKeyHandle.set(sessionKey);
	}
	
	public static UserInfoInnerVO getUserInfoInnerVO() {
		return userInfoInnerVOHandle.get();
	}

	public static void setUserInfoInnerVO(UserInfoInnerVO extUserInfoInnerVO) {
		AppAuthContext.userInfoInnerVOHandle.set(extUserInfoInnerVO);
	}

	public static void clear(){
		userInfoInnerVOHandle.remove();
		sessionKeyHandle.remove();
	}
	
}
