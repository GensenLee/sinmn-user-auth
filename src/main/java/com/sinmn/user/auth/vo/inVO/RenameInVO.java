package com.sinmn.user.auth.vo.inVO;

import com.sinmn.core.utils.verify.VerifyField;
import com.sinmn.core.utils.vo.BaseBean;
import lombok.Data;

/**
 * @author Gensen.Lee
 * @date 2019/4/26 10:22
 */
@Data
public class RenameInVO extends BaseBean {

    @VerifyField("新的用户名")
    private String newName;

}
