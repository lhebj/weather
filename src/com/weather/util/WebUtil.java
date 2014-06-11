package com.weather.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.userdetails.UserDetails;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class WebUtil {
	private static Log log = LogFactory.getLog(WebUtil.class);
	
	public static final String GUEST = "GUEST";

	protected static Log logger = LogFactory.getLog(WebUtil.class);

	@Deprecated
	public static String getPassword(){
		return getPassword(null);
	}
	
	@Deprecated
	public static String getPassword(HttpServletRequest request) {
		return null;
	}
	
	/**
	 * 设置cookie
	 * 
	 */
	public static void setCookie(HttpServletResponse response, String key, String value){
		setCookie(response, key, value, null);
	}
	
	/**
	 * 设置cookie
	 * maxAge == null表示使用默认有效时长，即仅在关闭浏览器前有效
	 */
	public static void setCookie(HttpServletResponse response, String key, String value, Integer maxAge){
		setCookie(response, key, value, maxAge, null, "/");
	}
	
	public static void setCookie(HttpServletResponse response, String key, String value, Integer maxAge, String domain, String path){
		Cookie cookie = new Cookie(key, value);
		if(maxAge != null){
			cookie.setMaxAge(maxAge);
		}
		if(!StringUtil.isNull(domain)){
			cookie.setDomain(domain);
		}
		if(!StringUtil.isNull(path)){
			cookie.setPath(path);
		}
		response.addCookie(cookie);
	}
	
	/**
	 * 获取cookie
	 */
	public static Cookie getCookie(HttpServletRequest request, String key){
		if(StringUtil.isNull(key)){
			return null;
		}
		Cookie[] cookies = request.getCookies();
		if(cookies == null){
			return null;
		}
		for(Cookie cookie: cookies){
			if(key.equals(cookie.getName())){
				return cookie;
			}
		}
		return null;
	}
	
	/**
	 * 获取cookie的值
	 */
	public static String getCookieValue(HttpServletRequest request, String key){
		Cookie cookie = getCookie(request, key);
		if(cookie == null){
			return "";
		}
		return cookie.getValue();
	}
	
	/**
	 * 删除cookie
	 */
	public static void removeCookie(HttpServletResponse response, String key){
		removeCookie(response, key, null, "/");
	}
	
	public static void removeCookie(HttpServletResponse response, String key, String domain, String path){
		setCookie(response, key, "", 0, domain, path);
	}
	
	public static String getUserId(HttpServletRequest request) {
		// FIXME fix it further
		String userName = GUEST;
		SecurityContext ctx = SecurityContextHolder.getContext();
		log.debug("ctx:" + ctx);
		if (ctx != null) {
			Authentication auth = ctx.getAuthentication();
			log.debug("auth:" + auth);
			if (auth != null) {
				Object principal = auth.getPrincipal();
				if (principal instanceof UserDetails) {
					userName = ((UserDetails) principal).getUsername();
				}
			}
		}
		log.debug("username:" + userName);
		return userName;

	}
	
	public static boolean isGuest() {
		boolean isGuest = getUserId(null).equalsIgnoreCase(GUEST);
		return isGuest;
	}

	/**
	 * 获取request下的UTF-8编码过的完整URL（包含queryString）
	 * 
	 * @param request
	 * @return
	 */
	public static String getEncodedRequestURL(HttpServletRequest request) {
		String currentURL = getRequestURL(request);
		if (StringUtil.isNull(currentURL))
			return currentURL;
		try {
			currentURL = URLEncoder.encode(currentURL, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return currentURL;
	}
	
	/**
	 * 获取request下的完整URL（包含queryString）
	 * 
	 * @param request
	 * @return
	 */
	public static String getRequestURL(HttpServletRequest request) {
		String currentURL = null;
		if (request == null)
			return currentURL;
		currentURL = (String) request.getAttribute("currentURL");
		return currentURL;
	}
	
}
