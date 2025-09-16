package com.evlink.global.aop;

public class UserAgentUtils {
	public static String parseAgent(String userAgent) {
		String os = "Unknown OS";
		String browser = "Unknown Browser";
		if(userAgent.contains("Window NT")) {
			os = "windows";
		}else if(userAgent.contains("Mac OS X")) {
			os = "macOS";
		}else if(userAgent.contains("Android")) {
			os = "Android";
		}else if(userAgent.contains("iPhone") || userAgent.contains("iPad")) {
			os = "iOS";
		}
		
		if(userAgent.contains("Edg/")) {
			browser = "Edge";
		}else if(userAgent.contains("Chrome/") && !userAgent.contains("Edg/")) {
			browser = "Chrome";
		}else if(userAgent.contains("Firefox/")) {
			browser = "Firefox";
		}else if(userAgent.contains("Safari/") && !userAgent.contains("Chrome/")) {
			browser = "Safari";
		}
		return os + " - " + browser;
	}
}
