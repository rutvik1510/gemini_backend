package org.hartford.eventguard.dto;

public class WeatherRiskResult {

    private Double temperature;
    private Double windSpeed;
    private Double humidity;
    private Double precipitation;
    private Double weatherRiskScore;

    public WeatherRiskResult() {
    }

    public WeatherRiskResult(Double temperature, Double windSpeed, Double humidity,
                            Double precipitation, Double weatherRiskScore) {
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.humidity = humidity;
        this.precipitation = precipitation;
        this.weatherRiskScore = weatherRiskScore;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Double getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(Double precipitation) {
        this.precipitation = precipitation;
    }

    public Double getWeatherRiskScore() {
        return weatherRiskScore;
    }

    public void setWeatherRiskScore(Double weatherRiskScore) {
        this.weatherRiskScore = weatherRiskScore;
    }
}
