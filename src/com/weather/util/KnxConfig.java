/**
 * 
 */
package com.weather.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class KnxConfig {
	private static InputStream in = null;
	public static Properties config = new Properties();
	public static InitialContext initialContext = null; 
	public static final Pattern serverNamePattern = Pattern.compile("(?:http://|https://)(.*?)(?:/|:\\d|$)");
	
	//排序
	public static int MAX_RANK = (int) Math.pow(2, 30);
	public static int INIT_RANK = (int) Math.pow(2, 28);
	public static int INIT_STEP = (int) Math.pow(2, 13);
	
	//upload
	//10M
	public static long MAX_UPLOADSIZE = 10485760L;
	//50K
	public static long MAX_UPLOADSIZE_PHOTO = 51200L;

	public static String FILE_SOURCE_DIRECTORY = "";
	public static String STATIC_URL = "";
	

	// mail
	public static Boolean MAIL_SWITCH = true;
	public static String MAIL_HOST = "smtp.gmail.com";
	public static String MAIL_PORT = "25";
	public static String MAIL_SMTP_AUTH = "true";
	public static String MAIL_SMTP_TIMEOUT = "25000";
	public static String EMAIL_ADMIN = "provence@163.com";
	public static String EMAIL_PERSONAL_EN = "Provence";
	public static String EMAIL_PERSONAL_CN = "Provence";
	public static String MAIL_USERNAME = "";
	public static String MAIL_PASSWORD = "";
	
	public static String EMAIL_RECIPIENT="";
	
	
	
	// memcached server
	// 192.168.3.41:11211,192.168.3.42:11211,192.168.3.43:11211
	public static String MEMCACHED_SERVER = "";
	// 1,3,2
	public static String MEMCACHED_WEIGHT = "";
	public static String CONNECTION_POOLSIZE = "";
	public static String MAX_TRY_TIMES = "";
	
	
	public static boolean USE_IMAGEMAGICK = false;
	

	static {
		try {
			File file = new File(System.getProperty("catalina.home") + "/conf/KnxConfig.properties");
			if (file.exists()) {
				in = new FileInputStream(file);
			} else {
				/**
				 * use default config
				 */
				in = KnxConfig.class.getClassLoader().getResourceAsStream("KnxConfig.properties");
			}
			config.load(in);


			// mail
			MAIL_SWITCH = new Boolean(config.getProperty("MAIL_SWITCH"));
			MAIL_HOST = config.getProperty("MAIL_HOST");
			MAIL_PORT = config.getProperty("MAIL_PORT");
			MAIL_SMTP_AUTH = config.getProperty("MAIL_SMTP_AUTH");
			MAIL_SMTP_TIMEOUT = config.getProperty("MAIL_SMTP_TIMEOUT");
			EMAIL_ADMIN = config.getProperty("EMAIL_ADMIN").trim();
			EMAIL_PERSONAL_EN = config.getProperty("EMAIL_PERSONAL_EN").trim();
			EMAIL_PERSONAL_CN = config.getProperty("EMAIL_PERSONAL_CN").trim();
			
			MAIL_USERNAME = config.getProperty("MAIL_USERNAME").trim();
			MAIL_PASSWORD = config.getProperty("MAIL_PASSWORD").trim();
			
			EMAIL_RECIPIENT = config.getProperty("EMAIL_RECIPIENT").trim();

			MEMCACHED_SERVER = config.getProperty("MEMCACHED_SERVER").trim();
			MEMCACHED_WEIGHT = config.getProperty("MEMCACHED_WEIGHT").trim();
			CONNECTION_POOLSIZE = config.getProperty("CONNECTION_POOLSIZE").trim();
			MAX_TRY_TIMES = config.getProperty("MAX_TRY_TIMES").trim();
			
			FILE_SOURCE_DIRECTORY = config.getProperty("FILE_SOURCE_DIRECTORY").trim();
			
			STATIC_URL = config.getProperty("STATIC_URL").trim();
			
			MAX_UPLOADSIZE = Long.parseLong(config.getProperty("MAX_UPLOADSIZE").trim());
			MAX_UPLOADSIZE_PHOTO = Long.parseLong(config.getProperty("MAX_UPLOADSIZE_PHOTO").trim());
			USE_IMAGEMAGICK = Boolean.parseBoolean(config.getProperty("USE_IMAGEMAGICK"));
			
			try {
				initialContext = new InitialContext();
				Context ctx = (Context) initialContext.lookup("java:comp/env/");
			} catch (NamingException e) {
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
