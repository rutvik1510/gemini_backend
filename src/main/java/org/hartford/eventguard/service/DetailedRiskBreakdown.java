package org.hartford.eventguard.service;

import org.hartford.eventguard.dto.WeatherRiskResponse;

public class DetailedRiskBreakdown {

    private double eventRisk;
    private double weatherRisk;
    private WeatherRiskResponse weatherData;

    public DetailedRiskBreakdown() {
    }

    public DetailedRiskBreakdown(double eventRisk, double weatherRisk, WeatherRiskResponse weatherData) {
        this.eventRisk = eventRisk;
        this.weatherRisk = weatherRisk;
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

    public WeatherRiskResponse getWeatherData() {
        return weatherData;
    }

    public void setWeatherData(WeatherRiskResponse weatherData) {
        this.weatherData = weatherData;
    }
}
