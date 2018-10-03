package com.foundersrooms.config;

import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestFilter implements Filter{
	private final List<String> allowedOrigins = Arrays.asList("/ec2-18-219-121-53.us-east-2.compute.amazonaws.com/d3n7igiax8d5lq.cloudfront.net","/ec2-18-219-121-53.us-east-2.compute.amazonaws.com","ec2-18-219-121-53.us-east-2.compute.amazonaws.com","https://d3n7igiax8d5lq.cloudfront.net","http://d3n7igiax8d5lq.cloudfront.net","http://localhost:4200","http://cloud.foundersrooms.com.s3-website.us-east-2.amazonaws.com","https://d2y9acxo4vooch.cloudfront.net","http://d2y9acxo4vooch.cloudfront.net","http://cloud.foundersroomsbeta.com.s3-website.us-east-2.amazonaws.com","http://foundersrooms.com","http://www.foundersrooms.com","https://foundersrooms.com","https://www.foundersrooms.com");
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		
		//to be unmasked if things get worse
		//response.setHeader("Access-Control-Allow-Origin", "*");
		//added
		String origin = request.getHeader("Origin");
		response.setHeader("Access-Control-Allow-Origin", allowedOrigins.contains(origin) ? origin : "");
		response.setHeader("Vary", "Origin");
		//end code added
		response.setHeader("Access-control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
		response.setHeader("Access-Control-Allow-Headers", "x-requested-with, x-auth-token");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		
		if(!(request.getMethod().equalsIgnoreCase("OPTIONS"))) {
			try {
				chain.doFilter(req, res);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Pre-fight");
			response.setHeader("Access-Control-Allowed-Methods", "POST, GET, DELETE");
			response.setHeader("Access-Control-Max-Age", "3600");
			response.setHeader("Access-Control-Allow-Headers", "authorization, content-type, x-auth-token, " +
                    "access-control-request-headers,access-control-request-method,accept,origin,authorization,x-requested-with");
			response.setStatus(HttpServletResponse.SC_OK);
		}
	}
	
	public void init(FilterConfig filterConfig) {}
	
	public void destroy() {}
}
