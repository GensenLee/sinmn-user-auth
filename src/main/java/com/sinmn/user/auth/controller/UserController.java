package com.sinmn.user.auth.controller;

import com.sinmn.core.utils.exception.CommonException;
import com.sinmn.core.utils.vo.ApiResult;
import com.sinmn.user.auth.context.UserAuthContext;
import com.sinmn.user.auth.services.UserService;
import com.sinmn.user.auth.vo.inVO.LoginInVO;
import com.sinmn.user.auth.vo.inVO.RepasswdInVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class UserController {


    @Autowired
    private UserService userService;


    @PostMapping("/api/common/user/login.do")
	public ApiResult<Object> login(@RequestBody LoginInVO extUserLoginInVO, HttpServletRequest req)
			throws CommonException
	{
		return ApiResult.getSuccess("登录成功", userService.login(extUserLoginInVO,req));
	}

	@PostMapping("/api/auth/user/rePasswd.do")
	public ApiResult<Object> rePasswd(@RequestBody RepasswdInVO extUserRepasswdInVO)
			throws CommonException
	{
		return ApiResult.getSuccess(userService.rePasswd(extUserRepasswdInVO, UserAuthContext.getUserInfoInnerVO()));
	}
	
}
