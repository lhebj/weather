package com.weather;

public class WeatherData  implements java.io.Serializable {
	/**
	 * TODO
	 */
	private static final long serialVersionUID = 7273745293739889882L;
	String weather ;
	String date ;
	String weekDay;
	String lowTemp;
	String highTemp;
	String currentTemp;
	String imgCode;
	String pm25;
	String city;
	public String getWeather() {
		return weather;
	}
	public void setWeather(String weather) {
		this.weather = weather;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getWeekDay() {
		return weekDay;
	}
	public void setWeekDay(String weekDay) {
		this.weekDay = weekDay;
	}
	public String getLowTemp() {
		return lowTemp;
	}
	public void setLowTemp(String lowTemp) {
		this.lowTemp = lowTemp;
	}
	public String getHighTemp() {
		return highTemp;
	}
	public void setHighTemp(String highTemp) {
		this.highTemp = highTemp;
	}
	public String getCurrentTemp() {
		return currentTemp;
	}
	public void setCurrentTemp(String currentTemp) {
		this.currentTemp = currentTemp;
	}
	public String getImgCode() {
		return imgCode;
	}
	public void setImgCode(String imgCode) {
		this.imgCode = imgCode;
	}
	public String getPm25() {
		return pm25;
	}
	public void setPm25(String pm25) {
		this.pm25 = pm25;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	@Override
	public String toString() {
		return "WeatherData [weather=" + weather + ", date=" + date + ", weekDay=" + weekDay + ", lowTemp=" + lowTemp
				+ ", highTemp=" + highTemp + ", currentTemp=" + currentTemp + ", imgCode=" + imgCode + ", pm25=" + pm25
				+ ", city=" + city + "]";
	}		
}
