package com.sinmn.user.auth.redis;

import com.sinmn.core.utils.redis.AspectRedisDao;
import com.sinmn.core.utils.redis.annotation.Redis;
import com.sinmn.core.utils.util.FastJsonUtils;
import com.sinmn.core.utils.util.StringUtil;
import com.sinmn.user.auth.vo.innerVO.UserInfoInnerVO;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

@Component
public class UserRedisDao extends AspectRedisDao {

	private String KEY_TEMPLATE = "SINMN.APP.USER.AUTH.SESSION_KEY:%s";

	private int expireTime = 60*60*24;
	
	@Redis
	public UserInfoInnerVO getUserInfo(String sessionKey){
		Jedis jedis = getJedis();
		String key = String.format(KEY_TEMPLATE, sessionKey);
		String value = jedis.get(key);
		if(StringUtil.isNotEmpty(value)){
			//重新设置过期时间
			jedis.setex(key, expireTime, value);
		}
		return FastJsonUtils.getBean(value, UserInfoInnerVO.class);
	}
	
	
	@Redis
	public void setUserInfo(UserInfoInnerVO extUserInfoInnerVO){
		Jedis jedis = getJedis();
		String key = String.format(KEY_TEMPLATE, extUserInfoInnerVO.getSessionKey());
		jedis.setex(key, expireTime, extUserInfoInnerVO.toJsonString());
	}

    @Redis
    public String get(String key){
        Jedis jedis = getJedis();
        String k = String.format(KEY_TEMPLATE, key);
        String value = jedis.get(k);
        if(StringUtil.isNotEmpty(value)){
            //重新设置过期时间
            jedis.setex(k, expireTime, value);
        }
        return value;
    }

    @Redis
    public void set(String key, String value){
        Jedis jedis = getJedis();
        String k = String.format(KEY_TEMPLATE, key);
        jedis.setex(k, expireTime, value);
    }

}
