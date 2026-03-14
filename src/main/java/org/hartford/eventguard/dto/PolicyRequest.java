package org.hartford.eventguard.dto;

import org.hartford.eventguard.entity.EventDomain;

public class PolicyRequest {

    private String policyName;
    private String description;
    private EventDomain domain;
    private Double baseRate;
    private Double maxCoverageAmount;
    private Boolean coversTheft;
    private Boolean coversWeather;
    private Boolean coversFire;
    private Boolean coversCancelation;

    public PolicyRequest() {}

    public Boolean getCoversTheft() {
        return coversTheft;
    }

    public void setCoversTheft(Boolean coversTheft) {
        this.coversTheft = coversTheft;
    }

    public Boolean getCoversWeather() {
        return coversWeather;
    }

    public void setCoversWeather(Boolean coversWeather) {
        this.coversWeather = coversWeather;
    }

    public Boolean getCoversFire() {
        return coversFire;
    }

    public void setCoversFire(Boolean coversFire) {
        this.coversFire = coversFire;
    }

    public Boolean getCoversCancelation() {
        return coversCancelation;
    }

    public void setCoversCancelation(Boolean coversCancelation) {
        this.coversCancelation = coversCancelation;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EventDomain getDomain() {
        return domain;
    }

    public void setDomain(EventDomain domain) {
        this.domain = domain;
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
}