package org.hartford.eventguard.dto;

import org.hartford.eventguard.entity.EventDomain;

public class PolicyResponse {

    private Long policyId;
    private String policyName;
    private String description;
    private EventDomain domain;
    private Double baseRate;
    private Double maxCoverageAmount;
    private Boolean isActive;

    public PolicyResponse() {
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
