//package com.practice.aspect;
//
//import org.aspectj.lang.annotation.AfterReturning;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.aspectj.lang.annotation.Pointcut;
//
///**
//* User: tomer
//*/
//@Aspect
//public class MyAspect {
//
//    @Before("execution(public * getMy*(..))")
//    public void beforeGetMethod() {
//        System.out.println("before method");
//    }
//
//    @AfterReturning(pointcut="execution(public * getMy*(..))", returning="retVal")
//    public void afterMethod(Object retVal) {
//        System.out.println("after method, retVal is: " + retVal);
//    }
//
//    @Before("com.practice.aspect.MyAspect.pointCt() && args(newValue,..)")
//    public void beforeSetMethod(Integer newValue) {
//        System.out.println("before set method, new value is: " + newValue);
//    }
//
//    @Pointcut("execution(public * setMy*(..))")
//    private void pointCt() {}
//}
