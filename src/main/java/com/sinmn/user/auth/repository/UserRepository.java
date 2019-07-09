package com.sinmn.user.auth.repository;

import com.sinmn.core.model.annotation.ModelAutowired;
import com.sinmn.core.model.core.AbstractModelRepository;
import com.sinmn.core.model.core.Model;
import com.sinmn.user.auth.model.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository extends AbstractModelRepository<User> {

	@ModelAutowired
	private Model<User> userModel;
	
	@Override
	protected Model<User> getModel() {
		return userModel;
	}
}
