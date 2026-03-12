package org.hartford.eventguard.dto;

import org.hartford.eventguard.entity.EventDomain;

public class PolicyResponse {

    private Long policyId;
    private String policyName;
    private String description;
    private EventDomain domain;
    private Double baseRate;
    private Double maxCoverageAmount;
    private Double deductible;
    private Boolean coversTheft;
    private Boolean coversWeather;
    private Boolean coversFire;
    private Boolean coversCancelation;
    private Boolean isActive;

    public PolicyResponse() {
    }

    public Double getDeductible() {
        return deductible;
    }

    public void setDeductible(Double deductible) {
        this.deductible = deductible;
    }

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

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
