package com.hyb.seckill.vo;

import com.hyb.seckill.validator.IsMobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @Author hyb
 * @Date 2025/3/8 17:27
 * @Version 1.0
 */
//登录参数
@Data
public class LoginVo {

    @NotNull
    @IsMobile
    private String mobile;

    @NotNull
    @Length(min = 32)
    private String password;
}
