package com.hyb.seckill.exception;

import com.hyb.seckill.vo.RespBeanEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author hyb
 * @Date 2025/3/8 20:39
 * @Version 1.0
 */
//全局异常处理:
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalException extends RuntimeException{
    private RespBeanEnum respBeanEnum;
}
