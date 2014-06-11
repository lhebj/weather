package com.weather.aqi;

import java.net.URLEncoder;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.ehcache.Element;

import com.weather.cache.CacheClient;
import com.weather.util.DateUtil;
import com.weather.util.HttpClientUtil;
import com.weather.util.LogUtil;

/**
 * 中国人民共和国环保部数据中心
 */
public class MepGovAQI implements AQI{

	public static String BEIJING_CODE = "北京市";
	private String dataQueryUrl = "http://datacenter.mep.gov.cn/report/airDairyCityHourAction.do?city={0}&startdate={1}&location=rq";
//	private String dataQueryUrl = "http://datacenter.mep.gov.cn/report/air_daily/airDairyCityHourMain.jsp?city={0}";

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
			String date = null;
			String oneHourBefore = DateUtil.deSerialize(DateUtil.getBefore(new Date(), 1, DateUtil.HOUR), "yyyy-MM-dd HH:mm").replaceFirst(":[\\d]{2}", ":00");
			date = oneHourBefore;	
			
			String qurl = dataQueryUrl.replace("{0}", URLEncoder.encode(cityCode, "GBK")).replace("{1}", URLEncoder.encode(date, "GBK"));
			String aqiInfo = HttpClientUtil.getInstance().getResponseByGetMethod(qurl, "GBK", false);
			
			if(aqiInfo.indexOf("AQI指数为")<0){
				String twoHourBefore = DateUtil.deSerialize(DateUtil.getBefore(new Date(), 2, DateUtil.HOUR), "yyyy-MM-dd HH:mm").replaceFirst(":[\\d]{2}", ":00");
				date = twoHourBefore;
				qurl = dataQueryUrl.replace("{0}", URLEncoder.encode(cityCode, "GBK")).replace("{1}", URLEncoder.encode(date, "GBK"));
				aqiInfo = HttpClientUtil.getInstance().getResponseByGetMethod(qurl, "GBK", false);
			}
			if(aqiInfo.indexOf("AQI指数为")<0){
				LogUtil.log.info("MepGovAQI aqi info: " + aqiInfo);
				return;
			}
			aqiInfo=aqiInfo.substring(aqiInfo.indexOf("AQI指数为")).replaceAll("\\s", "").replaceAll("/n", "");
			LogUtil.log.info("MepGovAQI aqi info: " + aqiInfo);
			Pattern pattern = Pattern.compile("AQI指数为[\\s\\S]*?[1234567890]*");
			Matcher m = pattern.matcher(aqiInfo);
			if (m.find()) {
				aqiInfo = m.group();
				LogUtil.log.info("MepGovAQI match aqi info: " + aqiInfo);
				AQIData myAQIData = new AQIData();
				String aqi = aqiInfo.substring(aqiInfo.indexOf("AQI指数为") + 6);
				myAQIData.setAqi(aqi);
				myAQIData.setCity(cityCode);
				myAQIData.setDate(date);
				Element element = new Element(cityCode, myAQIData);
				CacheClient.weatherCache.put(element);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@Override
	public String cityCodeAdapt(String geoCity){
		return geoCity + "市";
	}
	
	public static void main(String[] args) {
		AQI aqi = new MepGovAQI();
		System.out.println(aqi.getCurrentAQI(BEIJING_CODE).getAqi());
		System.out.println(aqi.getCurrentAQI(BEIJING_CODE).getAqi());
		
	}

}
