package com.weather;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.weather.util.HttpClientUtil;
import com.weather.util.LogUtil;

public class WeatherComCnCityCode {

	private static final String DEFAULT_CLASSPATH_CONFIGURATION_FILE = "/citycode.properties";
	private static InputStream in = null;
	public static Properties config = new Properties();
	
	public static Map<String,String> CITY_CODE = new HashMap<String,String>();
	
	static  {
		try {
			ClassLoader standardClassloader = Thread.currentThread().getContextClassLoader();
			URL url = null;
			if (standardClassloader != null) {
	            url = standardClassloader.getResource(DEFAULT_CLASSPATH_CONFIGURATION_FILE);
	        }
	        if (url == null) {
	            url = WeatherComCnCityCode.class.getResource(DEFAULT_CLASSPATH_CONFIGURATION_FILE);
	        }
			String filepath = url.getPath();
			LogUtil.log.info("load ehcache config file : " + filepath);
			File file = new File(filepath);
			if (file.exists()) {
				in = new FileInputStream(file);
				config.load(in);
				for(Object key: config.keySet()){
					CITY_CODE.put(String.valueOf(key), config.get(key).toString());
				}
			}else{
				System.out.println("error: citycode.properties is not exist");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void init(){
		Map<String,String> map = new HashMap<String,String>();
		map.put("http://www.weather.com.cn/html/province/beijing.shtml","北京");
		   
	    map.put("http://www.weather.com.cn/html/province/tianjin.shtml","天津");
	    map.put("http://www.weather.com.cn/html/province/shanghai.shtml","上海");
	    map.put("http://www.weather.com.cn/html/province/hebei.shtml","河北");
	    map.put("http://www.weather.com.cn/html/province/henan.shtml","河南");
	    map.put("http://www.weather.com.cn/html/province/anhui.shtml","安徽");
	    map.put("http://www.weather.com.cn/html/province/zhejiang.shtml","浙江");
	    map.put("http://www.weather.com.cn/html/province/chongqing.shtml","重庆");
	    map.put("http://www.weather.com.cn/html/province/fujian.shtml","福建");
	    map.put("http://www.weather.com.cn/html/province/gansu.shtml","甘肃");
	    map.put("http://www.weather.com.cn/html/province/guangdong.shtml","广东");
	    map.put("http://www.weather.com.cn/html/province/guangxi.shtml","广西");
	    map.put("http://www.weather.com.cn/html/province/guizhou.shtml","贵州");
	    map.put("http://www.weather.com.cn/html/province/yunnan.shtml","云南");
	    map.put("http://www.weather.com.cn/html/province/neimenggu.shtml","内蒙古");
	    map.put("http://www.weather.com.cn/html/province/jiangxi.shtml","江西");
	    map.put("http://www.weather.com.cn/html/province/hubei.shtml","湖北");
	    map.put("http://www.weather.com.cn/html/province/sichuan.shtml","四川");
	    map.put("http://www.weather.com.cn/html/province/ningxia.shtml","宁夏");
	    map.put("http://www.weather.com.cn/html/province/qinghai.shtml","青海");
	    map.put("http://www.weather.com.cn/html/province/shandong.shtml","山东");
	    map.put("http://www.weather.com.cn/html/province/shan-xi.shtml","陕西");
	    map.put("http://www.weather.com.cn/html/province/shanxi.shtml","山西");
	    map.put("http://www.weather.com.cn/html/province/xinjiang.shtml","新疆");
	    map.put("http://www.weather.com.cn/html/province/xizang.shtml","西藏");
	    map.put("http://www.weather.com.cn/html/weather/101320101.shtml","香港");
	    map.put("http://www.weather.com.cn/html/province/taiwan.shtml","台湾");
	    map.put("http://www.weather.com.cn/html/weather/101330101.shtml","澳门");
	    map.put("http://www.weather.com.cn/html/province/hainan.shtml","海南");
	    map.put("http://www.weather.com.cn/html/province/hunan.shtml","湖南");
	    map.put("http://www.weather.com.cn/html/province/jiangsu.shtml","江苏");
	    map.put("http://www.weather.com.cn/html/province/heilongjiang.shtml","黑龙江");
	    map.put("http://www.weather.com.cn/html/province/jilin.shtml","吉林");
	    map.put("http://www.weather.com.cn/html/province/liaoning.shtml","辽宁");
	    map.put("http://www.weather.com.cn/static/html/weather_list.shtml","更多");

	    Pattern city_pattern = Pattern.compile("<dt>[\\s\\S]*?</dt>");	    
	    for(String key: map.keySet()){
	    	String weatherInfo = HttpClientUtil.getInstance().getResponseByGetMethod(key,"utf-8", false);
//			System.out.println("weatherInfo: "+weatherInfo);

			Matcher mat = city_pattern.matcher(weatherInfo);
			try{
				String matchInfo = null;
				String city = null;
				String code = null;
				while(mat.find()){ 
					matchInfo = mat.group(0);
					if(matchInfo.indexOf("http://www.weather.com.cn/weather/")>0){
						code = matchInfo.substring(matchInfo.indexOf("http://www.weather.com.cn/weather/")+34, matchInfo.indexOf(".shtml"));
						city = matchInfo.substring(matchInfo.indexOf("target=\"_blank\">")+16, matchInfo.indexOf("</a>"));
						System.out.println(city + ", " + code); 
					}
					
				} 

			}catch(Exception e){
				e.printStackTrace();
			}
	    }
	}

}
