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

    private Double deductible; // Amount customer pays out of pocket

    // Benefit Flags
    private Boolean coversTheft = false;
    private Boolean coversWeather = false;
    private Boolean coversFire = false;
    private Boolean coversCancelation = false;

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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}