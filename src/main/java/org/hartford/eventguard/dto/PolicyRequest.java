package org.hartford.eventguard.dto;

import org.hartford.eventguard.entity.EventDomain;

public class PolicyRequest {

    private String policyName;
    private String description;
    private EventDomain domain;
    private Double baseRate;
    private Double maxCoverageAmount;

    public PolicyRequest() {}

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