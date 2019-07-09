package com.sinmn.user.auth.main;

import com.sinmn.core.model.annotation.ModelAutowired;
import com.sinmn.core.model.core.Model;
import com.sinmn.core.model.main.AbstractModelMain;
import com.sinmn.user.auth.model.User;
import com.sinmn.user.auth.model.UserLoginLog;
import lombok.Data;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy(false)
@Data
public class UserAuthInitMain extends AbstractModelMain {

	
	@ModelAutowired
	private Model<User> userModel;
	
	@ModelAutowired
	private Model<UserLoginLog> authLoginLogModel;
	
}
