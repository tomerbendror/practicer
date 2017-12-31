//package com.practice.handler;
//
//import org.springframework.web.servlet.HandlerExceptionResolver;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.util.HashMap;
//
///**
// * User: tomer
// */
//public class CustomExceptionHandler implements HandlerExceptionResolver {
//
//    @Override
//    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
//        HashMap<String, Object> model = new HashMap<>();
//        model.put("message", ex.getMessage());
//        return new ModelAndView("tomer", model);
//    }
//}
