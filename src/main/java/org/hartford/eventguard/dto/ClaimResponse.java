package org.hartford.eventguard.dto;

import org.hartford.eventguard.entity.EventDomain;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ClaimResponse {

    // Claim info
    private Long claimId;
    private Double claimAmount;
    private String description;
    private String status;
    private LocalDateTime filedAt;

    // Customer info
    private String customerName;

    // Event info
    private String eventName;
    private EventDomain eventType;
    private LocalDate eventDate;
    private String location;
    private Integer numberOfAttendees;
    private Double budget;

    // Policy info
    private String policyName;
    private Double baseRate;
    private Double maxCoverageAmount;
    private Double premiumAmount;

    // Risk info
    private Double eventRisk;
    private Double weatherRisk;
    private Double totalRisk;
    private String riskLevel;

    // Weather info
    private Double temperature;
    private Double humidity;
    private Double windSpeed;
    private String weatherCondition;

    public ClaimResponse() {
    }

    // Claim info getters/setters
    public Long getClaimId() {
        return claimId;
    }

    public void setClaimId(Long claimId) {
        this.claimId = claimId;
    }

    public Double getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(Double claimAmount) {
        this.claimAmount = claimAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getFiledAt() {
        return filedAt;
    }

    public void setFiledAt(LocalDateTime filedAt) {
        this.filedAt = filedAt;
    }

    // Customer info getters/setters
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    // Event info getters/setters
    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public EventDomain getEventType() {
        return eventType;
    }

    public void setEventType(EventDomain eventType) {
        this.eventType = eventType;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getNumberOfAttendees() {
        return numberOfAttendees;
    }

    public void setNumberOfAttendees(Integer numberOfAttendees) {
        this.numberOfAttendees = numberOfAttendees;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    // Policy info getters/setters
    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public Double getBaseRate() {
        return baseRate;
    }

    public void setBaseRate(Double baseRate) {
        this.baseRate = baseRate;
    }

    public Double getMaxCoverageAmount() {
        return maxCoverageAmount;
    }

    public void setMaxCoverageAmount(Double maxCoverageAmount) {
        this.maxCoverageAmount = maxCoverageAmount;
    }

    public Double getPremiumAmount() {
        return premiumAmount;
    }

    public void setPremiumAmount(Double premiumAmount) {
        this.premiumAmount = premiumAmount;
    }

    // Risk info getters/setters
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

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    // Weather info getters/setters
    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }
}
