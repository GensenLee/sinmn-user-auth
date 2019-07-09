package com.sinmn.user.auth.configuration;

import com.sinmn.core.utils.redis.configuration.EnableRedis;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
	    "com.sinmn.user.auth.repository",
	    "com.sinmn.user.auth.redis"
	})
@EnableRedis
public class UserAuthRepositoryConfiguration {
  
}
