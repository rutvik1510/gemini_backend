package org.hartford.eventguard.dto;

public class ApproveClaimRequest {
    private Double approvedAmount;

    public ApproveClaimRequest() {}

    public Double getApprovedAmount() {
        return approvedAmount;
    }

    public void setApprovedAmount(Double approvedAmount) {
        this.approvedAmount = approvedAmount;
    }
}
