package com.weather;

import java.util.List;

import net.sf.ehcache.Element;

import com.weather.cache.CacheClient;
import com.weather.util.HttpClientUtil;

public class SosoWeather implements Weather {
	public static String BEIJING_CODE = "101010100";

	private String url = "http://www.soso.com/q?query={0}";

	@Override
	public WeatherData getCurrentDayWeather(String cityCode) {
		// TODO Auto-generated method stub
		if(CacheClient.weatherCache.get(cityCode) == null){
			this.fetchWeather(cityCode);
		}
		Element element = CacheClient.weatherCache.get(cityCode);
		return element!=null?(WeatherData)element.getObjectValue():null;
	}

	@Override
	public List<WeatherData> getWeeklyWeather(String cityCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void fetchWeather(String cityCode) {
		if (cityCode == null) {
			return;
		}
		try {
			String qurl = url.replace("{0}", cityCode);
			String weatherInfo = HttpClientUtil.getInstance().getResponseByGetMethod(qurl, "utf-8", false);
			WeatherData wd = new WeatherData();
			String city = weatherInfo.substring(weatherInfo.indexOf("\"city\":\"") + 8, weatherInfo.indexOf("\",\"cityid"));
			wd.setCity(city);

			String currentTemp = weatherInfo.substring(weatherInfo.indexOf("\"temp\":\"") + 8, weatherInfo.indexOf("\",\"WD"));
			wd.setCurrentTemp(currentTemp);
			
			Element element = new Element(cityCode, wd);
			CacheClient.weatherCache.put(element);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@Override
	public String cityCodeAdapt(String geoCity) {
		return geoCity;
	}

	/** 
	 * 描述该方法的功能及算法流程
	 *
	 * @autor: Administrator  2014-6-6 下午11:08:30
	 * @param args    
	 * @return void 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
