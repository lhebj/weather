package com.weather.test;

import com.weather.Weather;
import com.weather.WeatherComCnCityCode;
import com.weather.WeatherComCnWeather;
import com.weather.WeatherData;
import com.weather.YahooWeather;
import com.weather.aqi.AQI;
import com.weather.aqi.AQIData;
import com.weather.aqi.MepGovAQI;
import com.weather.aqi.SosoAQI;
import com.weather.geo.Geo;
import com.weather.geo.GeoData;
import com.weather.geo.SinaGeoImpl;

public class Test {


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String ip = "201.10.1.1";
		Test test = new Test();
		
//		test.testWeatherComCnCityCode();
//		
//		test.testSinaGeoImpl();
		
//		test.testMepGovAQI();
		
//		test.testSinaGeoAndSosoAQI(ip);
//		
//		test.testSinaGeoAndWeatherComCn(ip);
//		
//		ip = "123.119.53.131";
//		
//		test.testSinaGeoAndSosoAQI(ip);
//		
//		test.testSinaGeoAndWeatherComCn(ip);
//		
//		ip = "27.43.121.246";
//		
//		test.testSinaGeoAndSosoAQI(ip);
//		
//		test.testSinaGeoAndWeatherComCn(ip);
//		
		
		test.testYahooWeather();
		test.testYahooWeather();
		
		
	}
	
	public void testWeatherComCnCityCodeInit(){
		WeatherComCnCityCode wcc = new WeatherComCnCityCode();
		wcc.init();
	}
	
	public void testWeatherComCnCityCode(){
		for(String key: WeatherComCnCityCode.CITY_CODE.keySet()){
			System.out.println(key + " : " + WeatherComCnCityCode.CITY_CODE.get(key));
		}
	}
	
	public void testSinaGeoImpl(String ip){
		Geo geo = new SinaGeoImpl();
		GeoData geoData = geo.getGeoDataByIp(ip);
		System.out.println(geoData.getCountry()+", "+geoData.getProvince()+", "+geoData.getCity());
	}
	
	public void testWeatherComCn(){
		Weather weather = new WeatherComCnWeather();
		WeatherData weatherData = weather.getCurrentDayWeather(WeatherComCnWeather.BEIJING_CODE);
		System.out.println(weatherData.getCity()+", "+weatherData.getCurrentTemp());
	}
	
	public void testYahooWeather(){
		Weather weather = new YahooWeather();
		WeatherData weatherData = weather.getCurrentDayWeather(YahooWeather.PROVENCE_CODE);
		System.out.println(weatherData.getCity()+", "+weatherData.getCurrentTemp()+","+weatherData.getImgCode());
	}
	
	public void testSinaGeoAndWeatherComCn(String ip){
		Geo geo = new SinaGeoImpl();
		GeoData geoData = geo.getGeoDataByIp(ip);
//		System.out.println(geoData.getCountry()+", "+geoData.getProvince()+", "+geoData.getCity());
		Weather weather = new WeatherComCnWeather();
		WeatherData weatherData = weather.getCurrentDayWeather(weather.cityCodeAdapt(geoData.getCity()));
		System.out.println(weatherData.getCity()+", "+weatherData.getCurrentTemp());
	}
	
	public void testSinaGeoAndMepGovAQI(String ip){
		Geo geo = new SinaGeoImpl();
		GeoData geoData = geo.getGeoDataByIp(ip);
//		System.out.println(geoData.getCountry()+", "+geoData.getProvince()+", "+geoData.getCity());
		AQI aqi = new MepGovAQI();
		AQIData myAQIData = aqi.getCurrentAQI(aqi.cityCodeAdapt(geoData.getCity()));
		System.out.println(myAQIData.getCity()+", "+myAQIData.getAqi());
	}
	
	public void testSinaGeoAndSosoAQI(String ip){
		Geo geo = new SinaGeoImpl();
		GeoData geoData = geo.getGeoDataByIp(ip);
//		System.out.println(geoData.getCountry()+", "+geoData.getProvince()+", "+geoData.getCity());
		AQI aqi = new SosoAQI();
		AQIData myAQIData = aqi.getCurrentAQI(aqi.cityCodeAdapt(geoData.getCity()));
		System.out.println(myAQIData.getCity()+", "+myAQIData.getAqi());
	}

}
