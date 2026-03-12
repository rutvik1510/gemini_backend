package org.hartford.eventguard.dto;

public class WeatherRiskResponse {

    private Double temperature;
    private Double windSpeed;
    private Double rainProbability;
    private Double weatherRiskScore;

    public WeatherRiskResponse() {
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

    public Double getRainProbability() {
        return rainProbability;
    }

    public void setRainProbability(Double rainProbability) {
        this.rainProbability = rainProbability;
    }

    public Double getWeatherRiskScore() {
        return weatherRiskScore;
    }

    public void setWeatherRiskScore(Double weatherRiskScore) {
        this.weatherRiskScore = weatherRiskScore;
    }
}
