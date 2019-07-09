package com.sinmn.user.auth.configuration;

import com.sinmn.core.utils.redis.configuration.EnableRedis;
import com.sinmn.user.auth.interceptor.GlobalAuthInterceptor;
import com.sinmn.user.auth.interceptor.UserAuthInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ComponentScan(basePackages = {
        "com.sinmn.user.auth"
})
@EnableRedis
public class UserAuthConfiguration extends WebMvcConfigurerAdapter {

    @Bean
    public GlobalAuthInterceptor globalAuthInterceptor() {
        return new GlobalAuthInterceptor();
    }

    @Bean
    public UserAuthInterceptor userAuthInterceptor() {
        return new UserAuthInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(globalAuthInterceptor())
                .addPathPatterns("/**");
        registry.addInterceptor(userAuthInterceptor())
                .addPathPatterns("/**/auth/**");
        super.addInterceptors(registry);
    }
}
