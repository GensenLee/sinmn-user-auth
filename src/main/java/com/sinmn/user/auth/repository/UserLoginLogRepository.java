package com.sinmn.user.auth.repository;

import com.sinmn.core.model.annotation.ModelAutowired;
import com.sinmn.core.model.core.AbstractModelRepository;
import com.sinmn.core.model.core.Model;
import com.sinmn.user.auth.model.UserLoginLog;
import org.springframework.stereotype.Repository;

@Repository
public class UserLoginLogRepository extends AbstractModelRepository<UserLoginLog> {

	@ModelAutowired
	private Model<UserLoginLog> authLoginLogModel;
	
	@Override
	protected Model<UserLoginLog> getModel() {
		return authLoginLogModel;
	}
}
