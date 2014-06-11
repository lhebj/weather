package com.weather.geo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.ehcache.Element;

import com.weather.cache.CacheClient;
import com.weather.util.HttpClientUtil;
import com.weather.util.LogUtil;

/**
 * 
 * 通过新浪接口查询IP所属的城市
 * 
 */
public class SinaGeoImpl implements Geo {

	// http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=js&ip=115.29.49.201
	private String url = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=js&ip={0}";
	private final static String DEFAULT_COUNTRY="中国";
	private final static String DEFAULT_PROVINCE="北京";
	private final static String DEFAULT_CITY="北京";

	public GeoData getGeoDataByIp(String ip) {
		if(ip == null ||ip.equals("127.0.0.1")){
			LogUtil.log.info("use defaultGeo , cause ip : " + (ip==null?"null":ip));
			return this.defaultGeo();
		}
		if(CacheClient.weatherCache.get(ip) == null){
			GeoData geoData = new GeoData();
			String qurl = url.replace("{0}", ip);
			String geoInfo = HttpClientUtil.getInstance().getResponseByGetMethod(qurl, "utf-8", false);
			if (geoInfo == null || geoInfo.indexOf("\"country\":\"") < 0) {
				LogUtil.log.info("use defaultGeo , cause geoInfo : " + (geoInfo==null?"null":geoInfo));
				geoData = this.defaultGeo();
			}else {
				String country = UnicodeToString(geoInfo.substring(geoInfo.indexOf("\"country\":\"") + 11, geoInfo.indexOf("\",\"province")));
				String province = UnicodeToString(geoInfo.substring(geoInfo.indexOf("\"province\":\"") + 12, geoInfo.indexOf("\",\"city")));
				String city = UnicodeToString(geoInfo.substring(geoInfo.indexOf("\"city\":\"") + 8, geoInfo.indexOf("\",\"district")));
				String isp = UnicodeToString(geoInfo.substring(geoInfo.indexOf("\"isp\":\"") + 7, geoInfo.indexOf("\",\"type")));
				geoData.setCountry(String.valueOf(country));
				geoData.setProvince(String.valueOf(province));
				geoData.setCity(String.valueOf(city));
				geoData.setIp(ip);
				geoData.setIsp(String.valueOf(isp));
			}
			
			if(geoData.getCity() == null || geoData.getCity().equals("")){
				LogUtil.log.info("use defaultGeo , cause ip is " + ip + ", country is : " + geoData.getCountry() + ", province is " + geoData.getProvince());
				geoData =  this.defaultGeo();
			}
//			return geoData;
			Element element = new Element(ip, geoData);
			CacheClient.weatherCache.put(element);
		}
		Element element = CacheClient.weatherCache.get(ip);
		return (GeoData)element.getObjectValue();		
	}
	
	private GeoData defaultGeo(){
		GeoData geoData = new GeoData();
		geoData.setCountry(String.valueOf(DEFAULT_COUNTRY));
		geoData.setProvince(String.valueOf(DEFAULT_PROVINCE));
		geoData.setCity(String.valueOf(DEFAULT_CITY));
		return geoData;
	}

	 public static String UnicodeToString(String str) {
	        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");    
	        Matcher matcher = pattern.matcher(str);
	        char ch;
	        while (matcher.find()) {
	            ch = (char) Integer.parseInt(matcher.group(2), 16);
	            str = str.replace(matcher.group(1), ch + "");    
	        }
	        return str;
	    }
}
