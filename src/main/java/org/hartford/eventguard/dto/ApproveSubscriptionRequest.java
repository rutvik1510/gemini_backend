package org.hartford.eventguard.dto;

public class ApproveSubscriptionRequest {
    private Double premiumOverrideAmount;
    private String overrideReason;

    public ApproveSubscriptionRequest() {}

    public Double getPremiumOverrideAmount() {
        return premiumOverrideAmount;
    }

    public void setPremiumOverrideAmount(Double premiumOverrideAmount) {
        this.premiumOverrideAmount = premiumOverrideAmount;
    }

    public String getOverrideReason() {
        return overrideReason;
    }

    public void setOverrideReason(String overrideReason) {
        this.overrideReason = overrideReason;
    }
}
