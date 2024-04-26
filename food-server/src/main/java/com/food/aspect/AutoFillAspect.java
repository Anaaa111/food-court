package com.food.aspect;

import com.food.annotation.AutoFill;
import com.food.constant.AutoFillConstant;
import com.food.context.BaseContext;
import com.food.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 定义切点，定义aop要拦截哪些方法(被AutoFill注解表示过的方法)
     */
    @Pointcut("execution(* com.food.mapper.*.*(..)) && @annotation(com.food.annotation.AutoFill)")
    public void autoFillCutPoint(){}
    /**
     * 自定义通知方法
     */
    @Before("autoFillCutPoint()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行公共字段自动填充");
        // 获取到当前拦截的方法上的数据库操作类型(即@AutoFill上的值)
        // 获取方法签名，从而获得方法的一些信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取标注该方法的注解信息
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        // 获取注解的值，即数据库操作类型
        OperationType operationType = autoFill.value();

        // 获取到该方法的参数，即实体对象
        Object[] args = joinPoint.getArgs();
        if (args ==null || args.length == 0){
            return;
        }
        // 默认将实体对象放在参数列表中的第一位
        Object entity = args[0];
        // 准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();
        // 实体获取到以后就可以根据数据库操作类型调用set方法进行赋值
        if (operationType == OperationType.INSERT){
            try {
                // 由于对象是Object对象，无法调用子类的set方法,即通过反射获取set方法并调用
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                // 执行set方法
                setCreateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentId);
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (operationType == OperationType.UPDATE) {
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else {
            return;
        }
    }
}
