package com.weather.cache;

import java.net.URL;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import com.weather.util.LogUtil;

public class CacheClient {	
	private static final String DEFAULT_CLASSPATH_CONFIGURATION_FILE = "/ehcache.xml";
	public static Cache weatherCache ;
	
	static {
		ClassLoader standardClassloader = Thread.currentThread().getContextClassLoader();
		URL url = null;
		if (standardClassloader != null) {
            url = standardClassloader.getResource(DEFAULT_CLASSPATH_CONFIGURATION_FILE);
        }
        if (url == null) {
            url = CacheClient.class.getResource(DEFAULT_CLASSPATH_CONFIGURATION_FILE);
        }
		String file = url.getPath();
		LogUtil.log.info("load ehcache config file : "+file);
//		System.out.println("ehcache config : "+file);
		CacheManager manager = CacheManager.create(file);
		weatherCache = manager.getCache("weatherCache");
	}

}
