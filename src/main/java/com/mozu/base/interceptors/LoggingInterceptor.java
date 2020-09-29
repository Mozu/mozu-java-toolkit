package com.mozu.base.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.mozu.base.utils.LoggerContextManager;

/**
 * This interceptor adds information about this application in MDC context 
 * so that all the loggers have access to it.
 */
public class LoggingInterceptor extends HandlerInterceptorAdapter {

	/**
	 * This method sets the app info in MDC context.
	 */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    	LoggerContextManager.setApplicationLoggingContext(request);
		return true;
    }
    
    /**
     * Removes the application info from the MDC context.
     */
	public void afterCompletion(
			HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		LoggerContextManager.clearApplicationLoggingContext();
	}

	
}
