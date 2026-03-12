package org.hartford.eventguard.dto;

import java.time.LocalDateTime;

public class AdminSubscriptionResponse {

    private Long subscriptionId;
    private String eventName;
    private String customerName;
    private String policyName;
    private Double premiumAmount;
    private Double riskPercentage;
    private String status;
    private LocalDateTime requestedAt;

    public AdminSubscriptionResponse() {
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }
}
