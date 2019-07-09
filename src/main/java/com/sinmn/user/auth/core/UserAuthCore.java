package com.sinmn.user.auth.core;

import com.sinmn.core.utils.util.StringUtil;
import com.sinmn.mjar.ext.core.UserExtCore;
import com.sinmn.user.auth.redis.UserRedisDao;
import com.sinmn.user.auth.vo.innerVO.UserInfoInnerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Gensen.Lee
 * @date 2019/7/9 14:53
 */
@Component
public class UserAuthCore {

    @Value("${sinmn.auth.user.single-sign-on:false}")
    private String singleLogin;

    @Autowired
    private UserRedisDao userRedisDao;

    private String SESSION_KEY_CONTROL = "%s,%s";

    @Autowired
    private UserExtCore userExtCore;

    /** 管理登陆模式  单点/多点
     * @param userId
     * @param userInfoInnerVO
     */
    public void manageSession(Long userId, UserInfoInnerVO userInfoInnerVO){
        String sessionKey = userInfoInnerVO.getSessionKey();
        String activeKey = userRedisDao.getActiveKey(userId);
        /*多点登陆*/
        if ("false".equals(singleLogin)) {
            if (StringUtil.isNotEmpty(activeKey)) {
                //加入会话列表
                sessionKey = String.format(SESSION_KEY_CONTROL, sessionKey, activeKey);
            }

            /*单点登陆*/
        }else if("true".equals(singleLogin)){
            if (StringUtil.isNotEmpty(activeKey)) {
                //移除已有的会话
                userExtCore.removeSession(activeKey);
            }
        }
        userRedisDao.setActiveKey(userId,sessionKey);
        userExtCore.holdSeesion(sessionKey,userInfoInnerVO.toJsonString());

    }

}
