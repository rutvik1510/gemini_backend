package org.hartford.eventguard.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "claims")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long claimId;

    @ManyToOne
    @JoinColumn(name = "subscription_id", nullable = false)
    private PolicySubscription policySubscription;

    private String description;

    private Double claimAmount;

    private java.time.LocalDate incidentDate;

    private Double approvedAmount;
    private String evidenceDocPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_officer_id")
    private User assignedOfficer;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "VARCHAR(255)")
    private ClaimStatus status;

    private String rejectionReason;

    private LocalDateTime filedAt;

    private LocalDateTime resolvedAt;

    @ManyToOne
    @JoinColumn(name = "resolved_by")
    private User resolvedBy;

    public Claim() {}

    public Long getClaimId() {
        return claimId;
    }

    public void setClaimId(Long claimId) {
        this.claimId = claimId;
    }

    public PolicySubscription getPolicySubscription() {
        return policySubscription;
    }

    public void setPolicySubscription(PolicySubscription policySubscription) {
        this.policySubscription = policySubscription;
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

    public java.time.LocalDate getIncidentDate() {
        return incidentDate;
    }

    public void setIncidentDate(java.time.LocalDate incidentDate) {
        this.incidentDate = incidentDate;
    }

    public Double getApprovedAmount() {
        return approvedAmount;
    }

    public void setApprovedAmount(Double approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    public String getEvidenceDocPath() {
        return evidenceDocPath;
    }

    public void setEvidenceDocPath(String evidenceDocPath) {
        this.evidenceDocPath = evidenceDocPath;
    }

    public User getAssignedOfficer() {
        return assignedOfficer;
    }

    public void setAssignedOfficer(User assignedOfficer) {
        this.assignedOfficer = assignedOfficer;
    }

    public ClaimStatus getStatus() {
        return status;
    }

    public void setStatus(ClaimStatus status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public LocalDateTime getFiledAt() {
        return filedAt;
    }

    public void setFiledAt(LocalDateTime filedAt) {
        this.filedAt = filedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public User getResolvedBy() {
        return resolvedBy;
    }

    public void setResolvedBy(User resolvedBy) {
        this.resolvedBy = resolvedBy;
    }
}