package org.hartford.eventguard.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ClaimRequest {

    @NotNull(message = "Subscription ID is required")
    private Long subscriptionId;

    @NotBlank(message = "Description is required")
    @Size(min = 10, message = "Description must be at least 10 characters")
    private String description;

    @NotNull(message = "Claim amount is required")
    @Positive(message = "Claim amount must be positive")
    private Double claimAmount;

    private String evidenceDocPath;

    @NotNull(message = "Incident date is required")
    private LocalDate incidentDate;

    private LocalDateTime filedAt; // Optional manual override for dev

    public ClaimRequest() {}

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(Double claimAmount) {
        this.claimAmount = claimAmount;
    }

    public String getEvidenceDocPath() {
        return evidenceDocPath;
    }

    public void setEvidenceDocPath(String evidenceDocPath) {
        this.evidenceDocPath = evidenceDocPath;
    }

    public LocalDate getIncidentDate() {
        return incidentDate;
    }

    public void setIncidentDate(LocalDate incidentDate) {
        this.incidentDate = incidentDate;
    }

    public LocalDateTime getFiledAt() {
        return filedAt;
    }

    public void setFiledAt(LocalDateTime filedAt) {
        this.filedAt = filedAt;
    }
}
