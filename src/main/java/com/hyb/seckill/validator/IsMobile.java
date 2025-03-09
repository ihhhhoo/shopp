package com.hyb.seckill.validator;

import com.hyb.seckill.vo.IsMobileValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @Author hyb
 * @Date 2025/3/8 20:30
 * @Version 1.0
 * 自定义注解
 */
@Target({ElementType.METHOD,ElementType.FIELD,ElementType.ANNOTATION_TYPE,ElementType.CONSTRUCTOR,ElementType.PARAMETER,ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {IsMobileValidator.class})
public @interface IsMobile {
    boolean required() default true;
    Class<?>[] groups() default {};//默然参数
    Class<? extends Payload>[] payload() default {};//默认参数
}
