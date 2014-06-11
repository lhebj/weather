package com.weather;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.weather.util.HttpClientUtil;

/**
 * 
 * https://developer.yahoo.com/weather/
 * 
 * 
 * @version 1.0.0 YahooWeather.java
 */
public class YahooWeather implements Weather {
	public static String PROVENCE_CODE = "575609";
	public static String BEIJING_CODE = "2151330";
	private static long LAST_CACHE_TIME=0;
	private long MAX_CACHE_TIME = 3600 * 1000; //MilliSecond

	private static Map<String,List<WeatherData>> weatherMap = new HashMap<String,List<WeatherData>>();
	
	/**
	 * w for WOEID.
	 * The WOEID parameter w is required. Use this parameter to indicate the location for the weather forecast as a WOEID.
	 * u for degrees units (Fahrenheit or Celsius).	 * 
	 * Units for temperature (case sensitive)
	 * f: Fahrenheit
	 * c: Celsius
	 */
	
	private String queryUrl = "http://weather.yahooapis.com/forecastrss?w={0}&u=c";
	
	private final Pattern weather_pattern = Pattern.compile("\\<yweather:forecast.*\\/\\>");
	
	private static Map<String,String> weekMap = new HashMap<String,String>();
	
	static{
		weekMap.put("Mon", "星期一");
		weekMap.put("Tue", "星期二");
		weekMap.put("Wed", "星期三");
		weekMap.put("Thu", "星期四");
		weekMap.put("Fri", "星期五");
		weekMap.put("Sat", "星期六");
		weekMap.put("Sun", "星期天");
	}
	
	
	@Override
	public WeatherData getCurrentDayWeather(String cityCode) {
		// TODO Auto-generated method stub	
		// TODO cache
		long datetime = new Date().getTime();
		if (datetime - LAST_CACHE_TIME > MAX_CACHE_TIME) {
			if( weatherMap.get(cityCode) == null ||  weatherMap.get(cityCode).get(0) ==null){
				this.fetchWeather(cityCode);
				LAST_CACHE_TIME =  datetime;
			}
		}

		if(weatherMap.get(cityCode).size() < 0 ){
			return null;
		}
		return weatherMap.get(cityCode).get(0);
	}

	@Override
	public List<WeatherData> getWeeklyWeather(String cityCode) {
		// TODO Auto-generated method stub
		return weatherMap.get(cityCode);
	}
	
	@Override
	public void fetchWeather(String cityCode){
		if(cityCode == null ){
			return ;
		}
		String qurl = queryUrl.replace("{0}", cityCode);
		String weatherInfo = HttpClientUtil.getInstance().getResponseByGetMethod(qurl,"utf-8", false);
		Matcher m = weather_pattern.matcher(weatherInfo);
		try{			     
			if(m.find()){
				String[] info = m.group().split("/n");	
				List<WeatherData> weatherList = new ArrayList<WeatherData>();
				WeatherData wd ;
				for(String item: info){
					wd = new WeatherData();
					String day = item.substring(item.indexOf("day=\"")+5, item.indexOf("\" date"));
					wd.setWeekDay(weekMap.get(day));
					String date = item.substring(item.indexOf("date=\"")+6, item.indexOf("\" low="));
					wd.setDate(new SimpleDateFormat("yyyy.MM.dd").format(new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).parse(date)));
					String low = item.substring(item.indexOf("low=\"")+5, item.indexOf("\" high="));
					wd.setLowTemp(low);
					String high = item.substring(item.indexOf("high=\"")+6, item.indexOf("\" text="));
					wd.setHighTemp(high);
					String text = item.substring(item.indexOf("text=\"")+6, item.indexOf("\" code="));
					wd.setWeather(text);
//					String code = item.substring(item.indexOf("code=\"")+6, item.indexOf("\"\\/"));
//					System.out.println(code);
					weatherList.add(wd);
				}
				weatherMap.put(cityCode, weatherList);				
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	@Override
	public String cityCodeAdapt(String geoCity) {
		return geoCity;
	}
		
	public static void main(String[] args) {
		Weather weather = new YahooWeather();
		weather.fetchWeather("575609");
		
		long datetime = new Date().getTime();
		System.out.println(datetime);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		datetime = new Date().getTime();
		System.out.println(datetime);
	}

}
