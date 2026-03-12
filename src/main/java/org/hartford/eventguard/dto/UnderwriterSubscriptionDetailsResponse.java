package org.hartford.eventguard.dto;

import java.time.LocalDate;

public class UnderwriterSubscriptionDetailsResponse {

    // Subscription details
    private Long subscriptionId;
    private String eventName;
    private String eventType;
    private String customerName;
    private String policyName;
    private String policyDescription;
    private Double baseRate;
    private Double maxCoverageAmount;
    private Double premiumAmount;
    private Double riskPercentage;
    private String riskLevel;
    private String status;

    // Event details
    private String location;
    private LocalDate eventDate;
    private Integer numberOfAttendees;
    private Integer attendees;
    private Double budget;
    private String venueType;
    private Integer duration;
    private Integer durationInDays;

    // Risk inputs
    private Boolean isOutdoor;
    private Boolean alcoholAllowed;
    private Boolean fireworksUsed;
    private Boolean celebrityInvolved;
    private Boolean temporaryStructure;
    private String locationRiskLevel;
    private String securityLevel;

    // Weather details
    private Double temperature;
    private Double windSpeed;
    private Double humidity;
    private String weatherCondition;

    // Risk breakdown
    private Double eventRisk;
    private Double weatherRisk;
    private Double totalRisk;

    public UnderwriterSubscriptionDetailsResponse() {
    }

    // Subscription details getters/setters
    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getPolicyDescription() {
        return policyDescription;
    }

    public void setPolicyDescription(String policyDescription) {
        this.policyDescription = policyDescription;
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

    public Double getRiskPercentage() {
        return riskPercentage;
    }

    public void setRiskPercentage(Double riskPercentage) {
        this.riskPercentage = riskPercentage;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Event details getters/setters
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
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

    public Integer getAttendees() {
        return attendees;
    }

    public void setAttendees(Integer attendees) {
        this.attendees = attendees;
    }

    public String getVenueType() {
        return venueType;
    }

    public void setVenueType(String venueType) {
        this.venueType = venueType;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getDurationInDays() {
        return durationInDays;
    }

    public void setDurationInDays(Integer durationInDays) {
        this.durationInDays = durationInDays;
    }

    public Boolean getIsOutdoor() {
        return isOutdoor;
    }

    public void setIsOutdoor(Boolean isOutdoor) {
        this.isOutdoor = isOutdoor;
    }

    public Boolean getAlcoholAllowed() {
        return alcoholAllowed;
    }

    public void setAlcoholAllowed(Boolean alcoholAllowed) {
        this.alcoholAllowed = alcoholAllowed;
    }

    public Boolean getFireworksUsed() {
        return fireworksUsed;
    }

    public void setFireworksUsed(Boolean fireworksUsed) {
        this.fireworksUsed = fireworksUsed;
    }

    public Boolean getCelebrityInvolved() {
        return celebrityInvolved;
    }

    public void setCelebrityInvolved(Boolean celebrityInvolved) {
        this.celebrityInvolved = celebrityInvolved;
    }

    public Boolean getTemporaryStructure() {
        return temporaryStructure;
    }

    public void setTemporaryStructure(Boolean temporaryStructure) {
        this.temporaryStructure = temporaryStructure;
    }

    public String getLocationRiskLevel() {
        return locationRiskLevel;
    }

    public void setLocationRiskLevel(String locationRiskLevel) {
        this.locationRiskLevel = locationRiskLevel;
    }

    public String getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(String securityLevel) {
        this.securityLevel = securityLevel;
    }

    // Weather details getters/setters
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

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    // Risk breakdown getters/setters
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
}
