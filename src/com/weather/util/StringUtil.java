package com.weather.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

public final class StringUtil {
	protected static Log logger = LogFactory.getLog(StringUtil.class);
	public static final int EXPERIENCE_DISPLAY_LENGTH = 80;
	private static final Pattern ipPattern = Pattern.compile("^\\D*([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}).*");
	private static final Pattern domainPattern = Pattern.compile("(?<=.*?)(\\.[^\\.]*+\\.)(com|net|org|gov|cc|biz|info|cn|co|edu)(\\.(cn|hk|uk|jp|tw))*");
	private static final Pattern illegal_screen_name_pattern = Pattern.compile("[^a-zA-Z0-9-_\u4E00-\u9FA5]|^[_-]+");
	private static final Pattern mobile_pattern = Pattern.compile("^((13[0-9])|(14[5,7])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
	// private static final Pattern legal_screen_name_pattern =
	// Pattern.compile("([[a-zA-Z0-9\u4E00-\u9FA5]{1}][a-zA-Z0-9_\\-\u4E00-\u9FA5]{0,19})|(\\-{1}[a-zA-Z0-9_\\-\u4E00-\u9FA5]*[a-zA-Z0-9\u4E00-\u9FA5]+[a-zA-Z0-9_\\-\u4E00-\u9FA5]*)");

	// 如果反向代理的ip列表过长，则默认只截取前128个字符
	private static final int DEFAULT_XFORWARDED_IP_LIMIT = 128;

	public static String encodingFileName(String fileName) {
		String returnFileName = "";
		try {
			returnFileName = URLEncoder.encode(fileName, "UTF-8");
			returnFileName = StringUtils.replace(returnFileName, "+", "%20");
			returnFileName = new String(fileName.getBytes("GB2312"), "ISO8859-1");
			returnFileName = StringUtils.replace(returnFileName, " ", "%20");

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			if (logger.isWarnEnabled()) {
				logger.info("Don't support this encoding ...");
			}
		}
		return returnFileName;
	}

	public static int parseInteger(String str, int defaultNum) {
		if (isNumeric(str)) {
			int num = defaultNum;
			try {
				num = Integer.parseInt(str);
			} catch (Exception ignored) {
			}
			return num;
		} else {
			return defaultNum;
		}
	}

	public static long parseLong(String arg, long defaultNum) {
		if (isNumeric(arg)) {
			long num = defaultNum;
			try {
				num = Long.parseLong(arg);
			} catch (Exception ignored) {
			}
			return num;
		} else {
			return defaultNum;
		}
	}

	public static boolean parseBoolean(String arg, boolean defaultNum) {
		if (arg == null) {
			return defaultNum;
		} else if (arg.equalsIgnoreCase("true")) {
			return true;
		} else if (arg.equalsIgnoreCase("false")) {
			return false;
		} else {
			return defaultNum;
		}
	}

	public static boolean isNumeric(String str) {
		if (isNull(str))
			return false;
		Pattern pattern = Pattern.compile("[0-9]+");
		return pattern.matcher(str).matches();
	}

	public static boolean batchValidateStr(List<String> strList) {
		for (int i = 0; i < strList.size(); i++) {
			if (!isNumeric(strList.get(i)))
				return false;
		}
		return true;
	}

	public static boolean batchValidateStr(String str[]) {
		for (int i = 0; i < str.length; i++) {
			if (!isNumeric(str[i])) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNull(String str) {
		return (str == null) || (str.trim().length() == 0);
	}

	/**
	 * 判断用户名是否合法
	 * 
	 * @param username
	 * @return
	 */
	public static boolean checkUserNameLegal(String username, boolean isInternal) {
		if (isInternal) {
			if (username.length() < 7 || username.length() > 41) {
				return false;
			}
		} else {
			if (username.length() < 3 || username.length() > 20) {
				return false;
			}
		}
		Pattern pattern = null;
		pattern = Pattern.compile("([a-zA-Z]{1}[a-zA-Z0-9_\\-]*)|([0-9]{1}[a-zA-Z0-9_\\-]*[a-zA-Z_\\-]+[a-zA-Z0-9_\\-]*)");

		Matcher matcher = pattern.matcher(username);
		if (matcher.matches()) {
			return true;
		} else
			return false;
	}

	/**
	 * 判断邮箱是否合法
	 * 
	 * @date 2012-5-15 pjian 与前端判断条件保持一致
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		if (isNull(email)) {
			return false;
		}
		Pattern pattern = Pattern.compile("^([\\w]+)(.[\\w]+)*@([\\w-]+\\.){1,5}([A-Za-z]){2,4}$");
		Matcher matcher = pattern.matcher(email);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断QQ是否合法
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isQQ(String QQ) {
		Pattern pattern = Pattern.compile("[1-9][0-9]{4,19}");
		Matcher matcher = pattern.matcher(QQ);
		if (matcher.matches()) {
			return true;
		} else
			return false;
	}

	/**
	 * 判断身份证是否合法
	 * 
	 * @param IDNo
	 * @return
	 */
	public static boolean isIDNo(String IDNo) {
		Pattern pattern = Pattern.compile("[a-zA-Z0-9]{1,30}");
		Matcher matcher = pattern.matcher(IDNo);
		if (matcher.matches()) {
			return true;
		} else
			return false;
	}

	/**
	 * 判断昵称是否合法
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isRealName(String realName) {
		if (realName.length() > 20) {
			return false;
		}
		Matcher matcher = illegal_screen_name_pattern.matcher(realName);
		if (matcher.find()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 判断地址是否合法
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isAddress(String address) {
		Pattern pattern = Pattern.compile("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]");
		Matcher matcher = pattern.matcher(address);
		return !matcher.find();
	}

	/**
	 * 判断邮编是否合法
	 * 
	 * @param postcode
	 * @return
	 */
	public static boolean isPostcode(String postcode) {
		Pattern pattern = Pattern.compile("[0-9]{6}");
		Matcher matcher = pattern.matcher(postcode);
		if (matcher.matches()) {
			return true;
		} else
			return false;
	}

	/**
	 * 判断电话号码是否合法
	 * 
	 * @param postcode
	 * @return
	 */
	public static boolean isPhoneNumber(String phonenumber) {
		Pattern pattern = Pattern.compile("((^((13[0-9])|(14[5,7])|(15[^4,\\D])|(18[0-9]))\\d{8}$)|(^\\d{7,8}$)|(^0[1,2]{1}\\d{1}(-|_)?\\d{8}$)|(^0[3-9]{1}\\d{2}(-|_)?\\d{7,8}$)|(^0[1,2]{1}\\d{1}(-|_)?\\d{8}(-|_)(\\d{1,4})$)|(^0[3-9]{1}\\d{2}(-|_)?\\d{7,8}(-|_)(\\d{1,4})$))");
		Matcher matcher = pattern.matcher(phonenumber);
		if (matcher.matches()) {
			return true;
		} else
			return false;
	}

	/**
	 * 是否是默认输入
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isDefaultInput(String s) {
		Pattern pattern = Pattern.compile("\\-+");
		Matcher matcher = pattern.matcher(s);
		if (matcher.matches()) {
			return true;
		} else
			return false;
	}

	public static String getFileMimeType(String fileName) {
		if (isNull(fileName) || !fileName.contains(".")) {
			return "";
		} else {
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		}
	}

	/*
	 * public static String getPrefixFileName(String fileName) { if
	 * (!isNull(fileName)) { if (fileName.lastIndexOf(".") > 0) { fileName =
	 * fileName.substring(0, fileName.lastIndexOf(".")); } return fileName; }
	 * return ""; }
	 */
	public static String getPrefixFileName(String fileName) {
		String sub = "";
		String subSon = "";
		if (!isNull(fileName)) {
			if (fileName.lastIndexOf(".") > 0) {
				sub = fileName.substring(0, fileName.lastIndexOf("."));
				if (sub.lastIndexOf(".") < 0) {
					fileName = sub;
				} else {
					String suffix = sub.substring(sub.lastIndexOf("."));
					if (suffix.equalsIgnoreCase(".asc") || suffix.equalsIgnoreCase(".audioc")) {
						subSon = sub.substring(0, sub.lastIndexOf("."));
						if (subSon.lastIndexOf(".") > 0) {
							subSon = subSon.substring(0, subSon.lastIndexOf("."));
							fileName = subSon;
						} else {
							fileName = subSon;
						}
					} else {
						fileName = sub;
					}
				}
			}
			return fileName;
		}
		return "";
	}

	static private char[] char_digit_set = { '2', '3', '4', '5', '6', '7', '8', '9', 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'p', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'z', 'x', 'c', 'v', 'b', 'n', 'm' };

	public static String getRandomString(final int size) {
		Random random = new Random();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < size; i++) {
			buffer.append(char_digit_set[Math.abs(random.nextInt() % char_digit_set.length)]);
		}
		return buffer.toString();
	}

	static private char[] char_set = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

	public static String getRandomNumber(final int size) {
		Random random = new Random();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < size; i++) {
			buffer.append(char_set[Math.abs(random.nextInt() % char_set.length)]);
		}
		return buffer.toString();
	}

	public static Map<String, Object> getFileSize(final long originalSize) {
		float currentSize = originalSize;
		int times = 0;
		Map<String, Object> map = new HashMap<String, Object>();
		String[] currentUnit = { "B", "KB", "MB", "GB" };
		while (currentSize > 1000 && times < 4) {
			times++;
			currentSize = currentSize / 1024;
		}
		map.put("size", currentSize);
		map.put("unit", currentUnit[times]);
		return map;
	}

	public static List<Long> fromStringToLongListByComma(String ids) {
		List<Long> list = new ArrayList<Long>();
		if (!isNull(ids)) {
			String[] idArr = ids.split(",|，");
			for (int i = 0; i < idArr.length; i++) {
				if (isNumeric(idArr[i])) {
					list.add(Long.parseLong(idArr[i]));
				}
			}
		}
		return list;
	}

	public static String fromLongListToStringWithComma(List<Long> list) {
		StringBuffer sb = new StringBuffer();
		if (list != null && list.size() > 0) {
			for (Long id : list) {
				sb.append(id);
				sb.append(",");
			}
			return sb.toString().substring(0, sb.toString().length() - 1);
		} else {
			return null;
		}
	}

	public static String escapeSql(String str) {
		str = StringEscapeUtils.escapeSql(str);
		if (str.contains("_")) {
			str = str.replace("_", "\\_");
		}
		if (str.contains("%")) {
			str = str.replace("%", "\\%");
		}

		return str != null ? str.trim() : "";
	}

	public static List<String> fromStringToListByComma(String params) {
		List<String> list = new ArrayList<String>();
		if (!isNull(params)) {
			String[] paramArr = params.split(",|，");
			if (paramArr != null) {
				for (int i = 0; i < paramArr.length; i++) {
					list.add(paramArr[i]);
				}
			}
		}
		return list;
	}

	public static List<String> fromStringToListByCommaForTag(String params) {
		List<String> list = new ArrayList<String>();
		if (!isNull(params)) {
			String[] paramArr = params.split(",|，| |　");
			if (paramArr != null) {
				for (int i = 0; i < paramArr.length; i++) {
					list.add(paramArr[i]);
				}
			}
		}
		return list;
	}

	public static String filterSpecialCharacter(String s) {
		String regex = "&";
		return s.replaceAll("&", " ");
	}

	public static String transferSpecialCharacter(String s) {
		if (s.contains("&amp;")) {
			s = s.replaceAll("&amp;", "&");
		}
		if (s.contains("&ldquo;")) {
			s = s.replaceAll("&ldquo;", "\"");
		}
		if (s.contains("&rdquo;")) {
			s = s.replaceAll("&rdquo;", "\"");
		}
		if (s.contains("&quot;")) {
			s = s.replaceAll("&quot;", "\"");
		}
		if (s.contains("&nbsp;")) {
			s = s.replaceAll("&nbsp;", " ");
		}
		if (s.contains("&lt;")) {
			s = s.replaceAll("&lt;", "<");
		}
		if (s.contains("&apos;")) {
			s = s.replaceAll("&apos;", "\'");
		}
		if (s.contains("&gt;")) {
			s = s.replaceAll("&gt;", ">");
		}
		if (s.contains("&mdash;")) {
			s = s.replaceAll("&mdash;", "—");
		}
		if (s.contains("&hellip;")) {
			s = s.replaceAll("&hellip;", "…");
		}
		return s;
	}

	public static boolean checkEmail(String emails) {
		if (StringUtil.isNull(emails)) {
			return false;
		}
		String[] emailsArr = emails.split(",");
		for (int i = 0; i < emailsArr.length; i++) {
			if (!StringUtil.isEmail(emailsArr[i].trim())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断手机号phone是否正确格式
	 * 
	 * @param phone
	 * @return
	 */
	public static boolean checkMobilePhone(String phone) {
		if (isNull(phone)) {
			return false;
		}
		Matcher matcher = mobile_pattern.matcher(phone);

		if (matcher.matches()) {
			return true;
		}
		return false;
	}

	public static String filteHtmlTag(String originalStr) {
		return originalStr != null ? originalStr.replaceAll("<[^<]+?>", "") : originalStr;
	}

	public static String getRequestIp(HttpServletRequest request) {
		if (request == null) {
			return "127.0.0.1";
		}
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip != null) {
			Matcher m = ipPattern.matcher(ip);
			if (m.find()) {
				ip = m.group(1);
			}
		}
		return ip;
	}

	public static String getRequestXForwardedIps(HttpServletRequest request) {
		return getRequestXForwardedIps(request, DEFAULT_XFORWARDED_IP_LIMIT);
	}

	/**
	 * 获取完整的反向代理ip列表
	 * 
	 * @param limit
	 *            返回结果的最大长度限制
	 */
	public static String getRequestXForwardedIps(HttpServletRequest request, int limit) {
		if (request == null) {
			return null;
		}
		String xForwardedIps = request.getHeader("X-Forwarded-For");
		if (StringUtil.isNull(xForwardedIps)) {
			return null;
		}
		if (limit > 0 && xForwardedIps.length() >= limit) {
			xForwardedIps = xForwardedIps.substring(0, limit);
		}
		return xForwardedIps;
	}

	public static String getRequestLink(HttpServletRequest request) {
		String reqeustLink = request.getRequestURL().toString();
		if (request.getQueryString() != null) {
			reqeustLink += "?" + request.getQueryString();
		}
		return reqeustLink;
	}

	/**
	 * 获取一级域名
	 * 
	 * @param link
	 * @return
	 */
	public static String getMainDomain(String link) {
		URL url = null;
		try {
			url = new URL(link);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (url == null) {
			return "";
		}
		String host = url.getHost();
		String cookieDomain = "";

		Matcher m = domainPattern.matcher(host);
		if (m.find()) {
			cookieDomain = m.group(1) + m.group(2) + (m.group(3) == null ? "" : m.group(3));
		}
		return cookieDomain;
	}

	/**
	 * get link path
	 * 
	 * @param link
	 * @return
	 */
	public static String getLinkPath(String link) {
		if (link == null) {
			return "/";
		}
		URL url = null;
		try {
			url = new URL(link);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (url == null) {
			return "/";
		}
		String[] path = url.getPath().split("/");
		// if (path.length() > 1 && path.endsWith("/")) {
		// path = path.substring(0, path.length() - 1);
		// }
		if (path.length > 2) {
			return "/" + path[1];
		} else {
			return "/";
		}

	}

	public static String html2Text(String inputString) {
		String htmlStr = inputString; // 含html标签的字符串
		String textStr = "";
		java.util.regex.Pattern p_script;
		java.util.regex.Matcher m_script;
		java.util.regex.Pattern p_style;
		java.util.regex.Matcher m_style;
		
		java.util.regex.Pattern p_WordDocument;
		java.util.regex.Matcher m_WordDocument;
		
		java.util.regex.Pattern p_html;
		java.util.regex.Matcher m_html;

		try {
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
																										// }
			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
			
			String regEx_WordDocument = "<[\\s]*?w:WordDocument[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?w:WordDocument[\\s]*?>"; // 定义style的正则表达式{或<w:WordDocument[^>]*?>[\\s\\S]*?<\\/w:WordDocument>
																									// }
			String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式

			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll(""); // 过滤script标签

			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll(""); // 过滤style标签
			
			p_WordDocument = Pattern.compile(regEx_WordDocument, Pattern.CASE_INSENSITIVE);
			m_WordDocument = p_WordDocument.matcher(htmlStr);
			htmlStr = m_WordDocument.replaceAll(""); // 过滤w:WordDocument标签

			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); // 过滤html标签

			textStr = htmlStr;

		} catch (Exception e) {
			System.err.println("Html2Text: " + e.getMessage());
		}

		return textStr;// 返回文本字符串
	}
	
	public static String filterWordFormatAndSomeHTML (String inputString) {
		String htmlStr = inputString; // 含html标签的字符串
		String textStr = "";
		
		java.util.regex.Pattern pattern;
		java.util.regex.Matcher matcher;
		
		
//		java.util.regex.Pattern p_script;
//		java.util.regex.Matcher m_script;
//		java.util.regex.Pattern p_style;
//		java.util.regex.Matcher m_style;
//		
//		java.util.regex.Pattern p_WordDocument;
//		java.util.regex.Matcher m_WordDocument;
//		
//		java.util.regex.Pattern p_html;
//		java.util.regex.Matcher m_html;
//		
//		java.util.regex.Pattern p_instyle;
//		java.util.regex.Matcher m_instyle;
//		
//		java.util.regex.Pattern p_inclass;
//		java.util.regex.Matcher m_inclass;

		try {
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
																										// }
			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
			
			String regEx_WordDocument = "<[\\s]*?w:WordDocument[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?w:WordDocument[\\s]*?>"; // 定义style的正则表达式{或<w:WordDocument[^>]*?>[\\s\\S]*?<\\/w:WordDocument>
																									// }
			String regEx_html = "<!--[\\s\\S]*?-->"; // 过滤注释<!-- -->
			
			String regEx_instyle = "style=\"[\\s\\S]*?\""; //过滤style
			
			String regEx_inclass = "class=\"[\\s\\S]*?\""; //过滤class
			
			String regEx_lang = "lang=\"[\\s\\S]*?\""; //过滤lang
			
			String regEx_sub_start = "<sub>"; //过滤sub
			String regEx_sub_end = "</sub>"; //过滤sub
			
			String regEx_sup_start = "<sup>"; //过滤sup
			String regEx_sup_end = "</sup>"; //过滤sup
			
			String regEx_aname_end = "<a[\\s\\S]*?name=[^>]*?>"; //a name , not a href

			pattern = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(htmlStr);
			htmlStr = matcher.replaceAll(""); // 过滤script标签

			pattern = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(htmlStr);
			htmlStr = matcher.replaceAll(""); // 过滤style标签
			
			pattern = Pattern.compile(regEx_WordDocument, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(htmlStr);
			htmlStr = matcher.replaceAll(""); // 过滤w:WordDocument标签

			pattern = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(htmlStr);
			htmlStr = matcher.replaceAll(""); // 过滤注释
			
			pattern = Pattern.compile(regEx_instyle, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(htmlStr);
			htmlStr = matcher.replaceAll(""); // 过滤style
			
			pattern = Pattern.compile(regEx_inclass, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(htmlStr);
			htmlStr = matcher.replaceAll(""); // 过滤class
			
			pattern = Pattern.compile(regEx_lang, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(htmlStr);
			htmlStr = matcher.replaceAll(""); // 过滤lang
			
			pattern = Pattern.compile(regEx_sub_start, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(htmlStr);
			htmlStr = matcher.replaceAll(""); // 过滤sub
			
			pattern = Pattern.compile(regEx_sub_end, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(htmlStr);
			htmlStr = matcher.replaceAll(""); // 过滤sub
			
			pattern = Pattern.compile(regEx_sup_start, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(htmlStr);
			htmlStr = matcher.replaceAll(""); // 过滤sup
			
			pattern = Pattern.compile(regEx_sup_end, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(htmlStr);
			htmlStr = matcher.replaceAll(""); // 过滤sup

			pattern = Pattern.compile(regEx_aname_end, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(htmlStr);
			htmlStr = matcher.replaceAll(""); // //a name , not a href
			
			textStr = htmlStr.replaceAll("<(?!(img|table|/table|tr|/tr|td|/td|p|/p|br|a|/a|b|/b|i|/i|u|/u|strike|/strike|h1|/h1|h2|/h2|h3|/h3|hr))[^>]*>","");

		} catch (Exception e) {
			System.err.println("Html2Text: " + e.getMessage());
		}

		return textStr;// 返回文本字符串
	}

	/**
	 * 获得指定长度toLength的子字符串(toLength小于等于0时不截取字符串)，然后将其中影响html格式的字符转义
	 * 
	 * @autor: hyx May 2, 2013 4:28:52 PM
	 * @param originalStr
	 * @param toLength
	 * @return
	 * @return String
	 */
	public static String subStringAndTransferSpecialCharacter(String originalStr, int toLength) {
		if (isNull(originalStr)) {
			return "";
		} else if (toLength <= 0) {
			;
		} else if (toLength > 0 && originalStr.length() > toLength) {
			originalStr = originalStr.substring(0, toLength - 1) + "…";
		}
		return escapeSpecialHTMLchar(originalStr);
	}

	public static String getShortStringByByte(String originalStr, int toLength) {
		if (isNull(originalStr)) {
			return "";
		}
		byte[] originalByte = null;
		byte[] abstractByte = null;
		// originalStr = transferSpecialCharacter(originalStr);
		// Pattern contentPattern = Pattern.compile("<\\s*([^>]*)\\s*>",
		// Pattern.CASE_INSENSITIVE);
		// Matcher contentMatcher = contentPattern.matcher(originalStr);
		// String abstractContent = contentMatcher.replaceAll("");
		originalByte = originalStr.getBytes();
		if (originalByte.length > toLength - 1) {
			abstractByte = new byte[toLength - 1];
			for (int i = 0; i < toLength - 1; i++) {
				abstractByte[i] = originalByte[i];
			}
			originalStr = new String(abstractByte);
			originalStr = originalStr.substring(0, originalStr.length() - 1) + "…";
		}
		return originalStr;
	}

	/**
	 * 去除注释标签
	 * 
	 * @param originalStr
	 * @return
	 */
	public static String formatString(String originalStr) {
		if (isNull(originalStr)) {
			return "";
		}
		originalStr = transferSpecialCharacter(originalStr);
		Pattern contentPattern = Pattern.compile("<\\!\\-\\-([\\s\\S]*)\\-\\->", Pattern.CASE_INSENSITIVE);
		Matcher contentMatcher = contentPattern.matcher(originalStr);
		String abstractContent = contentMatcher.replaceAll("");
		return abstractContent;
	}

	public static int getStringBytesLength(String name) {
		int l = 0;
		if (!isNull(name)) {
			byte[] b = name.getBytes();
			l = b.length;
		}
		return l;
	}

	/**
	 * 获取链接中的参数
	 */
	public static String getParameterFromLink(String link, String key) {
		if (link == null) {
			return null;
		}
		Map<String, String> param = new HashMap<String, String>();
		if (link.indexOf("?") > 0) {
			link = link.substring(link.indexOf("?") + 1);
			String[] paramArr = link.split("&");
			if (paramArr != null) {
				String p = "";
				String v = "";
				for (int i = 0; i < paramArr.length; i++) {
					int index = paramArr[i].indexOf("=");
					if (index > 0) {
						p = paramArr[i].substring(0, paramArr[i].indexOf("="));
						v = paramArr[i].substring(paramArr[i].indexOf("=") + 1);
						param.put(p, v);
					} else {
						index = paramArr[i].indexOf("%3D");
						if (index > 0) {
							p = paramArr[i].substring(0, paramArr[i].indexOf("%3D"));
							v = paramArr[i].substring(paramArr[i].indexOf("%3D") + 3);
							param.put(p, v);
						}
					}

				}
			}
		}
		return param.get(key);
	}

	public static String reverseString(String string) {
		StringBuffer reverse = new StringBuffer(string);
		return reverse.reverse().toString();
	}

	/**
	 * 去除源字符串中的全/半角逗号以及全角空格
	 * 
	 * @param originalString
	 * @return
	 */
	public static String replaceComma(String originalString) {
		if (!isNull(originalString)) {
			originalString = originalString.replaceAll(",", " ");
			originalString = originalString.replaceAll("，", " ");
			originalString = originalString.replaceAll("　", " ");
			originalString = originalString.replaceAll("、", " ");
			return originalString;
		} else
			return null;
	}

	/**
	 * 返回不包括空格的字符串所构成的list
	 * 
	 * @param originalString
	 * @return
	 */
	public static List<String> stringToList(String originalString) {
		if (!isNull(originalString)) {
			originalString = replaceComma(originalString);
			String[] stringArray = originalString.split(" ");
			List<String> stringList = new ArrayList<String>();
			for (String string : stringArray) {
				if (!string.equals(""))
					stringList.add(string);
			}
			return stringList;
		} else
			return null;
	}

	/**
	 * 将包含连续的多个空格（全/半角）的字符串缩短，都只用1个空格分隔
	 * 
	 * @param originalString
	 * @return
	 */
	public static String replaceLongSpace(String originalString) {
		if (!isNull(originalString)) {
			List<String> stringList = stringToList(originalString);
			if (stringList.size() > 0) {
				String newString = "";
				for (String s : stringList) {
					newString += s + " ";
				}
				newString = newString.substring(0, newString.length() - 1);
				return newString;
			} else
				return "";
		} else
			return null;
	}

	/**
	 * 判断一个字符串是不是一个中文单字
	 * 
	 * @author lhe
	 * @param word
	 * @return
	 */
	public static boolean isOneChineseWord(String word) {
		Matcher matcher = Pattern.compile("[\u4e00-\u9fa5]").matcher(word);
		if (word.getBytes().length == 1 || word.getBytes().length == 2) {
			if (matcher.find())
				return true;
			else
				return false;
		} else
			return false;
	}

	/**
	 * 过滤字符串中的\r\n
	 * 
	 * @param index
	 * @return
	 */
	public static String filterDefaultIndex(String index) {
		if (index == null) {
			return null;
		}
		char[] ca = index.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (char c : ca) {
			if (c != '\r' && c != '\n') {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * 过滤字符串中的\r\n
	 * 
	 * @param str
	 * @return
	 */
	public static String filterString(String str) {
		Pattern p = Pattern.compile("(\\r)?\\n");
		Matcher matcher = p.matcher(str);
		str = matcher.replaceAll("");
		return str;
	}

	// 返回带缩略图标签的content
	public static String getContentWithThumb(String originalString, int limit) {
		List<String> imgs = getContentPhotoURLList(originalString);
		String template = "<span class=\"examTip accountImg\" tipContent=\"\">&nbsp;</span>";
		String newString = replaceAllTagExceptImg(originalString);
		newString = newString.replaceAll("&nbsp;", " ").trim();
		newString = newString.replaceAll("&hellip;", "…");
		newString = replacePhotoURLs(newString, template);
		int stringLength = newString.length();
		int realLength = 0;
		int viewLength = 0;
		int length = 0;
		Pattern pattern = Pattern.compile("<span([^>]*)\\s*>([^>]*)span>");
		Matcher matcher = pattern.matcher(newString);
		int i = 0;
		int start = 0;
		int end = 0;
		while (matcher.find(i)) {
			start = matcher.start();
			end = matcher.end();
			if (start - realLength + viewLength <= limit) {
				length = start - realLength;
				realLength += length;
				viewLength += length;
			} else {
				length = limit - viewLength;
				realLength += length;
				viewLength += length;
				break;
			}
			if (viewLength + 2 > limit) {
				break;
			} else {
				viewLength += 2;
				realLength += end - start;
			}
			i = matcher.end();
		}
		if (realLength > start) {
			if (viewLength < limit) {
				realLength += limit - viewLength;
			}
		}
		if (realLength > newString.length()) {
			realLength = newString.length();
		}
		if (realLength == 0) {
			if (newString.length() <= limit)
				realLength = newString.length();
			else
				realLength = limit;
		}
		newString = newString.substring(0, realLength);
		if (realLength < stringLength)
			newString += "…";
		newString = insert(newString, imgs);
		return newString;
	}

	// 插入图片tag
	private static String insert(String str, List<String> imgs) {
		Pattern pattern = Pattern.compile("<span([^>]*)(tipContent=\"\")");
		Matcher matcher = pattern.matcher(str);
		while (matcher.find()) {
			str = matcher.replaceFirst(replaceTip(matcher.group(), imgs));
			matcher = pattern.matcher(str);
		}
		return str;
	}

	// 替换Tip
	private static String replaceTip(String str, List<String> imgs) {
		Pattern pattern = Pattern.compile("tipContent=\"\"");
		Matcher matcher = pattern.matcher(str);
		if (matcher.find()) {
			str = matcher.replaceFirst("tipContent=\"" + imgs.get(0) + "\"");
			imgs.remove(0);
		}
		return str;
	}

	// 置换所有非img标签
	private static String replaceAllTagExceptImg(String orininalString) {
		Pattern pattern = Pattern.compile("<[^(img)]([^>]*)\\s*>", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(orininalString);
		String newString = matcher.replaceAll("");
		return newString;
	}

	// 置换图片模板
	private static String replacePhotoURLs(String originalString, String template) {
		Pattern pattern = Pattern.compile("<img([^>]*)\\s*>", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(originalString);
		while (matcher.find()) {
			originalString = matcher.replaceFirst(template);
			matcher = pattern.matcher(originalString);
		}
		return originalString;
	}

	// 字符串转义
	private static List<String> getContentPhotoURLList(String str) {
		List<String> urls = new ArrayList<String>();
		Pattern pattern = Pattern.compile("<img([^>]*)\\s*>", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(str);
		while (matcher.find()) {
			String imgTag = matcher.group();
			imgTag = imgTag.replaceAll("\\<", "\\\\<");
			imgTag = imgTag.replaceAll("\\>", "\\\\>");
			imgTag = imgTag.replaceAll("\\\"", "\\\\\'");
			imgTag = imgTag.replaceAll("\\?", "\\\\?");
			imgTag = imgTag.replaceAll("\\$", "\\\\\\$");
			urls.add(imgTag);
		}
		return urls;
	}

	public static String matchInputBox(String s) {
		List<Integer> list = new ArrayList<Integer>();
		s = s.replaceAll("&mdash;", "_");
		Pattern p = Pattern.compile("[_|—]+");
		Matcher m1 = p.matcher(s);
		for (int i = 0; i < s.length();) {
			if (m1.find(i)) {
				int size = m1.end() - m1.start();
				list.add(size);
				i = m1.end();
			} else
				break;
		}
		for (int i = 0; i < list.size(); i++) {
			Matcher m2 = p.matcher(s);
			if (m2.find())
				s = m2.replaceFirst("<input type=\"text\" onpaste=\"return false;\" style=\"width:" + list.get(i) * 20 + "px;>\"/>");
		}
		return s;
	}

	public static String removeTag(String s) {
		Pattern p = Pattern.compile("<\\s*p\\s*>|<\\s*/p\\s*>", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(s);
		s = m.replaceAll("");
		return s;
	}

	public static String removeAllTag(String s) {
		Pattern p = Pattern.compile("<([^>]*)>", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(s);
		s = m.replaceAll("");
		return s;
	}

	/**
	 * 对能改变html结构的字符（&、"、<、>）做转义
	 * 
	 * @param str
	 * @return
	 */
	public static String escapeSpecialHTMLchar(String str) {
		str = str.replaceAll("<br/>|<br>", "\n"); // 保存回车
		str = str.replaceAll("&", "&amp;");
		str = str.replaceAll("\"", "&quot;");
		str = str.replaceAll("<", "&lt;");
		str = str.replaceAll(">", "&gt;");
		str = str.replaceAll(" ", "&nbsp;"); // 保存空格
		str = str.replaceAll("\n", "<br/>"); // 保存回车
		return str;
	}

	/**
	 * 对能改变html结构的字符（&、"、<、>）做转义（允许空格）
	 * 
	 * @param str
	 * @return
	 */
	public static String escapeSpecialHTMLcharAllowSpace(String str) {
		str = str.replaceAll("&", "&amp;");
		str = str.replaceAll("\"", "&quot;");
		str = str.replaceAll("<", "&lt;");
		str = str.replaceAll(">", "&gt;");
		str = str.replaceAll("\n", "<br/>"); // 保存回车
		return str;
	}

	/**
	 * 把html表示的"&amp;"、"&quot;"、"&lt;"、"&gt;"转化成&、"、<、>
	 * 
	 * @param str
	 * @return
	 */
	public static String deEscapeSpecialHTMLchar(String str) {
		str = str.replaceAll("&lt;", "<");
		str = str.replaceAll("&gt;", ">");
		str = str.replaceAll("&amp;", "&");
		str = str.replaceAll("&quot;", "\"");
		return str;
	}

	/**
	 * <p>
	 * Repeat a String <code>repeat</code> times to form a new String.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.repeat(null, 2) = null
	 * StringUtils.repeat(&quot;&quot;, 0) = &quot;&quot;
	 * StringUtils.repeat(&quot;&quot;, 2) = &quot;&quot;
	 * StringUtils.repeat(&quot;a&quot;, 3) = &quot;aaa&quot;
	 * StringUtils.repeat(&quot;ab&quot;, 2) = &quot;abab&quot;
	 * StringUtils.repeat(&quot;a&quot;, -2) = &quot;&quot;
	 * </pre>
	 * 
	 * @param str
	 *            the String to repeat, may be null
	 * @param repeat
	 *            number of times to repeat str, negative treated as zero
	 * @return a new String consisting of the original String repeated,
	 *         <code>null</code> if null String input
	 */
	public static String repeat(String str, int repeat) {
		if (str == null) {
			return null;
		}
		if (repeat <= 0) {
			return "";
		}
		int inputLength = str.length();
		if (repeat == 1 || inputLength == 0) {
			return str;
		}

		int outputLength = inputLength * repeat;
		switch (inputLength) {
		case 1:
			char ch = str.charAt(0);
			char[] output1 = new char[outputLength];
			for (int i = repeat - 1; i >= 0; i--) {
				output1[i] = ch;
			}
			return new String(output1);
		case 2:
			char ch0 = str.charAt(0);
			char ch1 = str.charAt(1);
			char[] output2 = new char[outputLength];
			for (int i = repeat * 2 - 2; i >= 0; i--, i--) {
				output2[i] = ch0;
				output2[i + 1] = ch1;
			}
			return new String(output2);
		default:
			StringBuffer buf = new StringBuffer(outputLength);
			for (int i = 0; i < repeat; i++) {
				buf.append(str);
			}
			return buf.toString();
		}
	}

	/**
	 * <p>
	 * Gets the rightmost <code>len</code> characters of a String.
	 * </p>
	 * <p>
	 * If <code>len</code> characters are not available, or the String is
	 * <code>null</code>, the String will be returned without an an exception.
	 * An exception is thrown if len is negative.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.right(null, *) = null
	 * StringUtils.right(*, -ve) = &quot;&quot;
	 * StringUtils.right(&quot;&quot;, *) = &quot;&quot;
	 * StringUtils.right(&quot;abc&quot;, 0) = &quot;&quot;
	 * StringUtils.right(&quot;abc&quot;, 2) = &quot;bc&quot;
	 * StringUtils.right(&quot;abc&quot;, 4) = &quot;abc&quot;
	 * </pre>
	 * 
	 * @param str
	 *            the String to get the rightmost characters from, may be null
	 * @param len
	 *            the length of the required String, must be zero or positive
	 * @return the rightmost characters, <code>null</code> if null String input
	 */
	public static String right(String str, int len) {
		if (str == null) {
			return null;
		}
		if (len < 0) {
			return "";
		}
		if (str.length() <= len) {
			return str;
		} else {
			return str.substring(str.length() - len);
		}
	}

	/**
	 * 标题中的关键字匹配并置红
	 * 
	 * @param keyWords
	 * @param title
	 * @return
	 */
	public static String replaceAllKeyWords(String keyWords, String title) {
		keyWords = keyWords.replaceAll("[\\[\\]\\^\\$\\.\\?\\*\\+\\(\\\\|\\{\\}%;]", "");
		Pattern pattern = Pattern.compile(keyWords, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(title);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) { // 查找符合pattern的字符串
			matcher.appendReplacement(sb, "<em style=\'color: #F00;\'>" + title.substring(matcher.start(), matcher.end()) + "</em>");
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	public static String convertSize(long size) {
		return convertSize(size, 1);
	}

	public static String convertSize(long size, int scale) {
		String retStr = "";
		if (size >= 1073741824) {
			retStr = round(size * 1.0 / 1073741824, scale, BigDecimal.ROUND_HALF_EVEN) + "GB";
		} else if (size >= 1048576) {
			retStr = round(size * 1.0 / 1048576, scale, BigDecimal.ROUND_HALF_EVEN) + "MB";
		} else if (size >= 1024) {
			retStr = round(size * 1.0 / 1024, scale, BigDecimal.ROUND_HALF_EVEN) + "KB";
		} else {
			retStr = round(size, scale, BigDecimal.ROUND_HALF_EVEN) + "B";
		}
		return retStr;
	}

	private static double round(double value, int scale, int roundingMode) {
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(scale, roundingMode);
		double retValue = bd.doubleValue();
		bd = null;
		return retValue;
	}

	public static float CHINESE_LENGTH = 1f;
	public static float OTHER_LENGTH = 0.6f;

	/**
	 * 一个中文字符的长度是CHINESE_LENGTH 一个其他字符的长度是OTHER_LENGTH 计算总和是否超过toLength
	 * 
	 * @param originalStr
	 * @param toLength
	 * @return
	 */
	public static String newShortString(String originalStr, int toLength) {
		if (isNull(originalStr)) {
			return "";
		}
		originalStr = transferSpecialCharacter(originalStr);
		Pattern contentPattern = Pattern.compile("<\\s*([^>]*)\\s*>", Pattern.CASE_INSENSITIVE);
		Matcher contentMatcher = contentPattern.matcher(originalStr);
		String abstractContent = contentMatcher.replaceAll("");
		float toChineselength = Float.parseFloat(String.valueOf(toLength > 0 ? toLength - 1 : 0));
		float length = 0f;
		char[] ch = abstractContent.toCharArray();
		int end = abstractContent.length() - 1;
		for (int i = 0; i < ch.length; i++) {
			if (length > toChineselength) {
				end = i - 1;
				break;
			}
			char c = ch[i];
			if (isChinese(c)) {
				length += CHINESE_LENGTH;
			} else {
				length += OTHER_LENGTH;
			}
		}

		return end < abstractContent.length() - 1 ? abstractContent.substring(0, end) + "…" : abstractContent;
	}

	/**
	 * GENERAL_PUNCTUATION 判断中文的“号 CJK_SYMBOLS_AND_PUNCTUATION 判断中文的。号
	 * HALFWIDTH_AND_FULLWIDTH_FORMS 判断中文的，号
	 */
	private static final boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	/**
	 * 将价格转换成string
	 * 
	 * @param used
	 * @param total
	 * @return
	 */
	public static String formatPrice(BigDecimal price) {
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(price);
	}

	/**
	 * 院校用户要求好友默认信息检测
	 */
	public static boolean isCollegeInvitationMsg(String msg, HttpServletRequest request) {
		String p = LocalizationUtil.getClientString("Message_Invitation_College_Message_Pattern", request);
		Pattern pattern = Pattern.compile(p);
		Matcher m = pattern.matcher(msg);
		return m.matches();
	}

	/**
	 * 将分钟转换成(小时:分钟)格式的字符串
	 * 
	 * @param minute
	 * @return
	 */
	public static String parseMinutesToHours(Integer minute) {
		StringBuffer buffer = new StringBuffer();
		if (minute < 60) {
			buffer.append("00:" + (minute >= 10 ? minute : ("0" + minute)));
		} else {
			Integer hour = minute / 60;
			Integer lastMinute = minute - hour * 60;
			buffer.append((hour >= 10 ? hour : ("0" + hour)) + ":" + (lastMinute >= 10 ? lastMinute : ("0" + lastMinute)));
		}
		return buffer.toString();
	}

	/**
	 * 过滤字符串中非中英文字符
	 * 
	 * @param target
	 * @return
	 */
	public static String filterNonEngAndChn(String target) {
		if (target == null || target.equals("")) {
			return "";
		}
		target = transferSpecialCharacter(target);
		return target.replaceAll("[^a-zA-Z\u4E00-\u9FA5]", "");
	}

	/**
	 * 对能改变html结构的字符（&、"、<、>）做转义
	 * 
	 * @param str
	 * @return
	 */
	public static String escapeSpecialHTMLcharForClassEdit(String str) {
		if (str != null) {
			str = str.replaceAll("<br>|<br/>", "\n"); // 保存回车
			str = str.replaceAll("&", "&amp;");
			str = str.replaceAll("\"", "&quot;");
			str = str.replaceAll("<", "&lt;");
			str = str.replaceAll(">", "&gt;");
			str = str.replaceAll(" ", "&nbsp;"); // 保存空格
			str = str.replaceAll("/", "&frasl;");
		}
		return str;
	}

	public static String getDirectoryName(String filePath) {
		if (isNull(filePath)) {
			return "";
		}
		return filePath.substring(0, filePath.lastIndexOf("/"));
	}

	public static String getOriginalFilePath(String filePath) {
		String fileName = getShortFileName(filePath);
		if (!isNull(fileName)) {
			int pos = fileName.lastIndexOf(".");
			if (fileName.lastIndexOf(".") > 0) {
				return getDirectoryName(filePath) + "/" + fileName.substring(0, pos) + "_raw" + fileName.substring(pos);
			}
		}
		return filePath;
	}

	public static String getShortFileName(String fileName) {
		if (!isNull(fileName)) {
			String oldFileName = new String(fileName);
			fileName = fileName.replace('\\', '/');
			if (fileName.endsWith("/")) {
				int idx = fileName.indexOf('/');
				if (idx == -1 || idx == fileName.length() - 1) {
					return oldFileName;
				} else {
					return oldFileName.substring(idx + 1, fileName.length() - 1);
				}

			}
			if (fileName.lastIndexOf("/") > 0) {
				fileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
			}

			return fileName;
		}
		return "";
	}
	
	public static void main(String[] args) {
		String test = "234<strong>324</strong>324<em>32<p>pppp</p><span>ssspppaaannn</span><a href=\"#\">4te</a>st1</em>2<img src=\"test.jpg\" />3<table border=\"1\"><tr><td>asdf</td></tr><tr><td>bbbb</td></tr></table>";
		System.out.println(test);
	    System.out.println(test.replaceAll("<(?!(img|table|/table|tr|/tr|td|/td|p|/p|span|/span))[^>]*>",""));
	}

}
