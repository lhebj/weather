package com.weather;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Element;

import com.weather.cache.CacheClient;
import com.weather.util.HttpClientUtil;
import com.weather.util.LogUtil;

public class WeatherComCnWeather implements Weather {
	public static String BEIJING_CODE = "101010100";

	private String skQueryUrl = "http://www.weather.com.cn/data/sk/{0}.html";
	private String dataQueryUrl = "http://www.weather.com.cn/data/cityinfo/{0}.html";

	private static Map<String, String> weekMap = new HashMap<String, String>();

	static {
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
		// long datetime = new Date().getTime();
		// if (datetime - LAST_CACHE_TIME > MAX_CACHE_TIME || weatherData ==
		// null) {
		// this.fetchWeather(cityCode);
		// LAST_CACHE_TIME = datetime;
		// }

		if (cityCode == null) {
			cityCode = BEIJING_CODE;
		}

		if (CacheClient.weatherCache.get(cityCode) == null) {
			this.fetchWeather(cityCode);
		}
		Element element = CacheClient.weatherCache.get(cityCode);
		return element != null ? (WeatherData) element.getObjectValue() : null;
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
			String qurl = dataQueryUrl.replace("{0}", cityCode);
			String weatherInfo = HttpClientUtil.getInstance().getResponseByGetMethod(qurl, "utf-8", false);
			WeatherData wd = new WeatherData();
			String city = weatherInfo.substring(weatherInfo.indexOf("\"city\":\"") + 8, weatherInfo.indexOf("\",\"cityid"));
			wd.setCity(city);
			// String low =
			// weatherInfo.substring(weatherInfo.indexOf("\"temp1\":\"") + 9,
			// weatherInfo.indexOf("℃\",\",\"temp2"));
			// wd.setLowTemp(low);
			// String high =
			// weatherInfo.substring(weatherInfo.indexOf("\"temp2\":\"") + 9,
			// weatherInfo.indexOf("℃\",\",\"weather"));
			// wd.setHighTemp(high);
			//
			// if(Integer.parseInt(low) > Integer.parseInt(high)){
			// wd.setHighTemp(low);
			// wd.setLowTemp(high);
			// }
			String weather = weatherInfo.substring(weatherInfo.indexOf("\"weather\":\"") + 11, weatherInfo.indexOf("\",\"img1"));
			wd.setWeather(weather);
			String imgCode = weatherInfo.substring(weatherInfo.indexOf("\"img1\":\"") + 8, weatherInfo.indexOf("\",\"img2\"")).replace("gif", "png");
			// System.out.println(code);
			// if(imgCode.contains("d")){
			// imgCode = "day/"+imgCode;
			// }else{
			// imgCode = "night/"+imgCode;
			// }

			if (imgCode.startsWith("n")) {
				imgCode = imgCode.replaceFirst("n", "d");
			}
			imgCode = "day/" + imgCode;
			wd.setImgCode(imgCode);

			String skqurl = skQueryUrl.replace("{0}", cityCode);
			weatherInfo = HttpClientUtil.getInstance().getResponseByGetMethod(skqurl, "utf-8", false);
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
		String cityCode = WeatherComCnCityCode.CITY_CODE.get(geoCity);
		if(cityCode == null){
			LogUtil.log.info(geoCity + " can be not parsed, use default ");
			cityCode = BEIJING_CODE;
		}
		return cityCode;
	}

	public static void main(String[] args) {
		Weather weather = new WeatherComCnWeather();
		WeatherData wd = weather.getCurrentDayWeather(BEIJING_CODE);
		System.out.println(wd.getCurrentTemp());
	}

}
