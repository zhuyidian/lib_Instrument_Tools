package com.coocaa.remoteplatform.core.request;

public class WeatherInfo {
    public String cityName;
    public String cityId;
    public String temperature;
    public String airQuality;
    public String realTimeWeather;
    public String dressingAdvice;
    public String humidity;

    public String weatherId;

    public String icon;//看给的参考代码决定是否显示白天还是晚上的（目前用不到）

    public String dressingAdviceDetail;

//    @Override
//    public String toString() {
//        final StringBuilder sb = new StringBuilder("WeatherInfo{");
//        sb.append("cityName='").append(cityName);
//        sb.append(", temperature='").append(temperature);
//        sb.append(", humidity='").append(humidity);
//        sb.append(", realTimeWeather='").append(realTimeWeather);
//        sb.append(", dressingAdvice='").append(dressingAdvice);
//        sb.append(", weatherId='").append(weatherId);
//        sb.append('}');
//        return sb.toString();
//    }


    @Override
    public String toString() {
        return "WeatherInfo--{" +
                "cityName='" + cityName + '\'' +
                ", cityId='" + cityId + '\'' +
                ", temperature='" + temperature + '\'' +
                ", airQuality='" + airQuality + '\'' +
                ", realTimeWeather='" + realTimeWeather + '\'' +
                ", dressingAdvice='" + dressingAdvice + '\'' +
                ", weatherId='" + weatherId + '\'' +
                ", icon='" + icon + '\'' +
                ", dressingAdviceDetail='" + dressingAdviceDetail + '\'' +
                '}';
    }
}
