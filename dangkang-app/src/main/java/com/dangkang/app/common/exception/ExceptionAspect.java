package com.dangkang.app.common.exception;

import com.baidu.unbiz.fluentvalidator.interceptor.FluentValidateInterceptor;
import com.dangkang.app.common.annotation.ApplicationService;
import com.dangkang.client.dto.response.AbstractResponse;
import com.dangkang.client.dto.response.MultipleResponse;
import com.dangkang.client.dto.response.Response;
import com.dangkang.domain.exception.ApplicationException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @date 2023/1/13 14:42
 */
@Aspect
@Component
@EnableAspectJAutoProxy(proxyTargetClass = true,exposeProxy = true)
public class ExceptionAspect {

    @Autowired
    private FluentValidateInterceptor fluentValidateInterceptor;

    public ExceptionAspect(){
    }
    public static final Logger logger = LoggerFactory.getLogger(ExceptionAspect.class);

//    @Pointcut("execution(* com.dangkang.app.customercontext.service.*.*(..))")
    @Pointcut(value="@annotation(com.dangkang.app.common.annotation.ExceptionResolver)")
    public void pointcut() {
    }

    @Around(value = "pointcut()")
    public Object around(ProceedingJoinPoint joinPoint){
        Object methodResponse;

        try{
            methodResponse = joinPoint.proceed();
        }catch (Throwable e){
            if(logger.isDebugEnabled()){
                logger.debug("around exception",e);
            }
            return resolveException(joinPoint,e);
        }
        return methodResponse;
    }

    private Object resolveException(JoinPoint joinPoint,Throwable t)  {
        AbstractResponse response;
        MethodSignature ms = (MethodSignature) joinPoint.getSignature();
        Method targetMethod = ms.getMethod();

       Class returnType = ms.getReturnType();
       if(returnType == Response.class){
           response = new Response<>();
       }else {
           response = new MultipleResponse<>();
       }

        String SERVICE_CODE = "";
        String SERVICE_NAME = "";
        ApplicationService applicationService = targetMethod.getAnnotation(ApplicationService.class);
        if(applicationService != null){
            SERVICE_CODE = applicationService.ServiceCode();
            SERVICE_NAME = applicationService.ServiceName();
        }

       if (t instanceof ApplicationException) {
           //处理应用异常
           ApplicationException ae = (ApplicationException) t;
           if (t.getCause() != null) {
               //应用异常是自定义或转换为ApplicationException，系统异常会内嵌在ApplicationException中
               logger.error(ae.getDetailMessage(), t); //系统环境出错

           } else {
               logger.warn(ae.getPromptMessage());//业务异常warn
           }
           response.buildFailure(SERVICE_CODE, SERVICE_NAME, ae.getErrorCode(), ae.getPromptMessage());
       } else {
           //未捕获的其他异常
           response.buildUnknownFailure(SERVICE_CODE, SERVICE_NAME, t.getMessage());
       }
       return response;
    }

}
