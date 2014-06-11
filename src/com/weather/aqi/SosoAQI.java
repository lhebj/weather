package com.weather.aqi;

import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.ehcache.Element;

import com.weather.cache.CacheClient;
import com.weather.util.HttpClientUtil;
import com.weather.util.LogUtil;

public class SosoAQI implements AQI {


	public static String BEIJING_CODE = "北京AQI";
	private String dataQueryUrl = "http://www.soso.com/q?ie=utf-8&sc=web&pid=-&cid=&query={0}";

	@Override
	public AQIData getCurrentAQI(String cityCode) {		
		if(CacheClient.weatherCache.get(cityCode) == null){
			this.fetchAQI(cityCode);
		}
		Element element = CacheClient.weatherCache.get(cityCode);
		return element != null ?(AQIData)element.getObjectValue(): null;
	}

	@Override
	public void fetchAQI(String cityCode) {
		if (cityCode == null) {
			return;
		}
		try {
			String qurl = dataQueryUrl.replace("{0}", URLEncoder.encode(cityCode, "utf-8"));
			String aqiInfo = HttpClientUtil.getInstance().getResponseByGetMethod(qurl, "utf-8", false);
//			System.out.println(aqiInfo);
//			Pattern pattern = Pattern.compile("AQI指数为[\\s\\S]*?[1234567890]*");
			Pattern pattern = Pattern.compile("class=\"pmBox\"[\\s\\S]*?</div>");
			Matcher m = pattern.matcher(aqiInfo);
			if (m.find()) {
				aqiInfo = m.group();
				LogUtil.log.info("soso aqi info: " + aqiInfo);
				AQIData myAQIData = new AQIData();
				String aqi = aqiInfo.substring(aqiInfo.indexOf("class=\"size_50\">") + 16, aqiInfo.indexOf("</td>/n<td class=\"wt_129\">"));
				myAQIData.setAqi(aqi);
				myAQIData.setCity(cityCode);
				Element element = new Element(cityCode, myAQIData);
				CacheClient.weatherCache.put(element);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@Override
	public String cityCodeAdapt(String geoCity){
		return geoCity + "AQI";
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AQI aqi = new SosoAQI();
		aqi.fetchAQI(BEIJING_CODE);
	}

}
