package com.weather.aqi;


public interface AQI {
	
	public AQIData getCurrentAQI(String cityCode);
	public void fetchAQI(String cityCode);
	public String cityCodeAdapt(String geoCity);
}
