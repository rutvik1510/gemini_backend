package org.hartford.eventguard.service;

import org.hartford.eventguard.dto.WeatherRiskResponse;

public class DetailedRiskBreakdown {

    private double eventRisk;
    private double weatherRisk;
    private String riskFactors;
    private WeatherRiskResponse weatherData;

    public DetailedRiskBreakdown() {
    }

    public DetailedRiskBreakdown(double eventRisk, double weatherRisk, String riskFactors, WeatherRiskResponse weatherData) {
        this.eventRisk = eventRisk;
        this.weatherRisk = weatherRisk;
        this.riskFactors = riskFactors;
        this.weatherData = weatherData;
    }

    public double getEventRisk() {
        return eventRisk;
    }

    public void setEventRisk(double eventRisk) {
        this.eventRisk = eventRisk;
    }

    public double getWeatherRisk() {
        return weatherRisk;
    }

    public void setWeatherRisk(double weatherRisk) {
        this.weatherRisk = weatherRisk;
    }

    public String getRiskFactors() {
        return riskFactors;
    }

    public void setRiskFactors(String riskFactors) {
        this.riskFactors = riskFactors;
    }

    public WeatherRiskResponse getWeatherData() {
        return weatherData;
    }

    public void setWeatherData(WeatherRiskResponse weatherData) {
        this.weatherData = weatherData;
    }
}
