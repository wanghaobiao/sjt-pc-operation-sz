package com.acrabsoft.web.service.sjt.pc.operation.web.util;


import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @description  耗时统计
 * @date  2021-2-4 17:28
 * @author  wanghb
 * @edit
 */
@Aspect
@Order(5)
@Component
public class WebLogAspect {

    private Logger logger = Logger.getLogger(getClass());
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Pointcut("execution(* com.acrabsoft.web.service.sjt.pc.operation.web.manager..*(..)  ) ||  execution(* com.acrabsoft.web.service.sjt.pc.operation.web.appManage..*(..)) ||  execution(* com.acrabsoft.web.service.sjt.pc.operation.web.system..*(..))")
    public static void webLog(){}

    /**
     * @description  统计接口耗时
     * @param  joinPoint
     * @param  joinPoint
     * @return  返回结果
     * @date  2021-2-4 17:37
     * @author  wanghb
     * @edit
     */
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        startTime.set(System.currentTimeMillis());
    }


    /**
     * @description  统计接口耗时
     * @param  joinPoint
     * @param  ret
     * @return  返回结果
     * @date  2021-2-4 17:37
     * @author  wanghb
     * @edit
     */
    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(JoinPoint joinPoint,Object ret) throws Throwable {
        String classNameStr = joinPoint.getSignature().getDeclaringType().getSimpleName();
        if(classNameStr.indexOf( "Controller" )  == -1 ){
            return;
        }
        StackTraceElement stackTraceElement = getStackTraceElement(joinPoint);
        System.err.println("[" + Thread.currentThread().getName() + "] "  + stackTraceElement + "耗时 : " + (PowerUtil.getBigDecimal( System.currentTimeMillis() - startTime.get()  ).divide( new BigDecimal( "1000" ) )+ "秒"));
    }


    /**
     * @description  ProceedingJoinPoint 转 StackTraceElement
     * @param  joinPoint 待转类型
     * @return  stackTraceElement  转换结果
     * @date  20/04/24 19:09
     * @author  wanghb
     * @edit
     */
    public static StackTraceElement getStackTraceElement(JoinPoint joinPoint) {
        //完整类名
        String declaringClass = (joinPoint.getSignature().getDeclaringType() + "").replace( "class ","" );
        //类名
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        //方法名
        String methodName = joinPoint.getSignature().getName();
        //文件名
        String fileName = new StringBuilder(className).append( ".java" ).toString();

        ClassPool pool = ClassPool.getDefault();
        CtMethod methodX = null;
        try {
            CtClass cc = pool.get(declaringClass);
            methodX = cc.getDeclaredMethod(methodName);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        int xlineNumber = methodX.getMethodInfo().getLineNumber(0);
        return new StackTraceElement(className, methodName, fileName, xlineNumber - 1);
    }

}
