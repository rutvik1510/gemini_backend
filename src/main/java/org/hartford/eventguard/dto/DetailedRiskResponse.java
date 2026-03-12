package org.hartford.eventguard.dto;

public class DetailedRiskResponse {

    private Double eventRisk;
    private Double weatherRisk;
    private Double totalRisk;
    private WeatherRiskResponse weatherData;
    private String riskBreakdown;

    public DetailedRiskResponse() {
    }

    public Double getEventRisk() {
        return eventRisk;
    }

    public void setEventRisk(Double eventRisk) {
        this.eventRisk = eventRisk;
    }

    public Double getWeatherRisk() {
        return weatherRisk;
    }

    public void setWeatherRisk(Double weatherRisk) {
        this.weatherRisk = weatherRisk;
    }

    public Double getTotalRisk() {
        return totalRisk;
    }

    public void setTotalRisk(Double totalRisk) {
        this.totalRisk = totalRisk;
    }

    public WeatherRiskResponse getWeatherData() {
        return weatherData;
    }

    public void setWeatherData(WeatherRiskResponse weatherData) {
        this.weatherData = weatherData;
    }

    public String getRiskBreakdown() {
        return riskBreakdown;
    }

    public void setRiskBreakdown(String riskBreakdown) {
        this.riskBreakdown = riskBreakdown;
    }
}
