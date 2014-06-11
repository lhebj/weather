package com.weather.util;

import java.security.MessageDigest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MD5Util {
	private final static Log log = LogFactory.getLog(MD5Util.class.getName());

	/**
	 * Use MD5 to encoding message.
	 * 
	 * @param message
	 * @return
	 */
	public static String getMD5(String message) {
		String md5 = "";
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			byte[] array = messageDigest.digest(message.getBytes("utf-8"));
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; i++) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).toLowerCase().substring(1, 3));
			}
			md5 = sb.toString();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return md5;
	}

	public static void main(String[] args) {
	}
}