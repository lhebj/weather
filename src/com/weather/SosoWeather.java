package com.weather;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.ehcache.Element;

import com.weather.cache.CacheClient;
import com.weather.util.HttpClientUtil;
import com.weather.util.LogUtil;

public class SosoWeather implements Weather {
	private static String BEIJING_CODE = "101010100";
	private static String DEFAULT_CITY = "北京";
	private static String QUERY_WEATHER = "天气";
	
	

	private String dataQueryUrl = "http://www.sogou.com/sogou?rfrom=soso&ie=utf-8&sc=web&pid=-&cid=&query={0}";

	private static Map<String, String> weekMap = new HashMap<String, String>();
	
	private static Map<String, String> weatherImgCodeMap = new HashMap<String, String>();

	static {
		weekMap.put("Mon", "星期一");
		weekMap.put("Tue", "星期二");
		weekMap.put("Wed", "星期三");
		weekMap.put("Thu", "星期四");
		weekMap.put("Fri", "星期五");
		weekMap.put("Sat", "星期六");
		weekMap.put("Sun", "星期天");
		
		weatherImgCodeMap.put("晴", "d0.png");
		weatherImgCodeMap.put("多云", "d1.png");
		weatherImgCodeMap.put("阴", "d2.png");
		weatherImgCodeMap.put("阵雨", "d3.png");
		weatherImgCodeMap.put("雷阵雨", "d4.png");
		weatherImgCodeMap.put("雷阵雨伴有冰雹", "d5.png");
		weatherImgCodeMap.put("雨夹雪", "d6.png");
		weatherImgCodeMap.put("小雨", "d7.png");
		weatherImgCodeMap.put("中雨", "d8.png");
		weatherImgCodeMap.put("大雨", "d9.png");
		weatherImgCodeMap.put("暴雨", "d10.png");
		weatherImgCodeMap.put("大暴雨", "d11.png");
		weatherImgCodeMap.put("特大暴雨", "d12.png");
		weatherImgCodeMap.put("阵雪", "d13.png");
		weatherImgCodeMap.put("小雪", "d14.png");
		weatherImgCodeMap.put("中雪", "d15.png");
		weatherImgCodeMap.put("大雪", "d16.png");
		weatherImgCodeMap.put("暴雪", "d17.png");
		weatherImgCodeMap.put("雾", "d18.png");
		weatherImgCodeMap.put("冻雨", "d19.png");
		weatherImgCodeMap.put("沙尘暴", "d20.png");
		weatherImgCodeMap.put("小雨转中雨", "d21.png");
		weatherImgCodeMap.put("中雨转大雨", "d22.png");
		weatherImgCodeMap.put("大雨转暴雨", "d23.png");
		weatherImgCodeMap.put("default", "undefined.png");
		weatherImgCodeMap.put("阵雨转阴", "d3.png");
	}

	/**
	 * cityCode: geoData.getCity()
	 */
	@Override
	public WeatherData getCurrentDayWeather(String cityCode) {
		// TODO Auto-generated method stub
		// TODO cache
		// long datetime = new Date().getTime();
		// if (datetime - LAST_CACHE_TIME > MAX_CACHE_TIME || weatherData ==
		// null) {
		// this.fetchWeather(cityCode);
		// LAST_CACHE_TIME = datetime;
		// }
		String city = cityCode == null ? DEFAULT_CITY : cityCode;

		if (CacheClient.weatherCache.get(city) == null) {
			LogUtil.log.info("getCurrentDayWeather miss cache");
			this.fetchWeather(city);
		}else{
			LogUtil.log.info("getCurrentDayWeather hit cache");
		}
		Element element = CacheClient.weatherCache.get(city);
		return element != null ? (WeatherData) element.getObjectValue() : null;
	}

	@Override
	public List<WeatherData> getWeeklyWeather(String cityCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void fetchWeather(String cityCode) {
		String city = cityCode == null ? DEFAULT_CITY : cityCode;
		try {
			String qurl = dataQueryUrl.replace("{0}", URLEncoder.encode(city+QUERY_WEATHER, "utf-8"));
			String weatherInfo = HttpClientUtil.getInstance().getResponseByGetMethod(qurl, "utf-8", false);
//			System.out.println(weatherInfo);
			WeatherData wd = new WeatherData();
			wd.setCity(city);
			Pattern pattern = Pattern.compile("class=\"wtInfo\" id=\"sogou_vr_20007311_sk_0\">[\\s\\S]*?</div>");
			Matcher m = pattern.matcher(weatherInfo);
			if (m.find()) {
				weatherInfo = m.group();
				System.out.println(weatherInfo);
				String weather = weatherInfo.substring(weatherInfo.indexOf("id=\"sogou_vr_20007311_sk_0\">") + 28,
						weatherInfo.indexOf("<i>"));
				wd.setWeather(weather);
				
				String currentTemp = weatherInfo.substring(weatherInfo.indexOf("<i>当前") + 5,
						weatherInfo.lastIndexOf("</i>"));
				wd.setCurrentTemp(currentTemp);

			}

			String imgCodeTmp = weatherImgCodeMap.get(wd.getWeather());
			String imgCode = "day/" + imgCodeTmp!=null?imgCodeTmp:weatherImgCodeMap.get("default");
			wd.setImgCode(imgCode);

			LogUtil.log.info("fetchWeather:" + wd.toString());

			Element element = new Element(city, wd);
			CacheClient.weatherCache.put(element);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public String cityCodeAdapt(String geoCity) {
		String cityCode = WeatherComCnCityCode.CITY_CODE.get(geoCity);
		if (cityCode == null) {
			LogUtil.log.info(geoCity + " can be not parsed, use default ");
			cityCode = BEIJING_CODE;
		}
		return cityCode;
	}

	public static void main(String[] args) {
		Weather weather = new SosoWeather();
		WeatherData wd = weather.getCurrentDayWeather(DEFAULT_CITY);
		System.out.println(wd.getCurrentTemp());
	}

}
