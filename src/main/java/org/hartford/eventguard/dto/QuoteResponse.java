package org.hartford.eventguard.dto;

public class QuoteResponse {

    private Long eventId;
    private Long policyId;
    private Double riskPercentage;
    private Double estimatedPremium;

    // Enhanced risk breakdown
    private Double baseRate;
    private Double eventRisk;
    private Double weatherRisk;
    private WeatherRiskResponse weatherData;
    private String venueType;

    public QuoteResponse() {
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public Double getRiskPercentage() {
        return riskPercentage;
    }

    public void setRiskPercentage(Double riskPercentage) {
        this.riskPercentage = riskPercentage;
    }

    public Double getEstimatedPremium() {
        return estimatedPremium;
    }

    public void setEstimatedPremium(Double estimatedPremium) {
        this.estimatedPremium = estimatedPremium;
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

    public WeatherRiskResponse getWeatherData() {
        return weatherData;
    }

    public void setWeatherData(WeatherRiskResponse weatherData) {
        this.weatherData = weatherData;
    }

    public String getVenueType() {
        return venueType;
    }

    public void setVenueType(String venueType) {
        this.venueType = venueType;
    }

    public Double getBaseRate() {
        return baseRate;
    }

    public void setBaseRate(Double baseRate) {
        this.baseRate = baseRate;
    }
}
