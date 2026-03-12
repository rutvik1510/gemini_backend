package org.hartford.eventguard.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "policies")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long policyId;

    @Column(nullable = false, unique = true)
    private String policyName;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventDomain domain;   // CORPORATE / ENTERTAINMENT

    @Column(nullable = false)
    private Double baseRate;      // Base premium %

    @Column(nullable = false)
    private Double maxCoverageAmount;

    @Column(nullable = false)
    private Boolean isActive = true;

    public Policy() {}

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

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}