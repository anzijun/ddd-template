package com.dangkang.app.validation;

import com.baidu.unbiz.fluentvalidator.interceptor.FluentValidateInterceptor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * @date 2023/1/30 15:58
 */
@Aspect
@Component
@EnableAspectJAutoProxy(proxyTargetClass = true,exposeProxy = true)
public class ValidationAspect {

    @Autowired
    private FluentValidateInterceptor fluentValidateInterceptor;

    @Pointcut(value="@annotation(com.dangkang.app.common.annotation.Validation)")
    public void pointcut() {
    }

    @Before("pointcut()")
    public void invokeValidate(JoinPoint joinPoint) throws Throwable{
        MethodInvocationProceedingJoinPoint methodInvocationProceedingJoinPoint = (MethodInvocationProceedingJoinPoint)joinPoint;
        ProxyMethodInvocation proxyMethodInvocation;
        Field methodInvocation = MethodInvocationProceedingJoinPoint.class.getDeclaredField("methodInvocation");
        // 避免出现不可访问异常
        methodInvocation.setAccessible(true);
        proxyMethodInvocation = (ProxyMethodInvocation)methodInvocation.get(methodInvocationProceedingJoinPoint);
        fluentValidateInterceptor.invoke(proxyMethodInvocation);
    }
}
