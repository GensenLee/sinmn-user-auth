package com.sinmn.user.auth.vo.inVO;

import com.sinmn.core.utils.verify.VerifyField;
import com.sinmn.core.utils.vo.BaseBean;
import lombok.Data;

@Data
public class LoginInVO extends BaseBean{

	@VerifyField(value = "账号")
	private String account;
	
	@VerifyField(value = "密码")
	private String passwd;
	
}
