package org.hartford.eventguard.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "policy_subscriptions")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PolicySubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subscriptionId;


    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;


    @ManyToOne
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;


    private Double riskPercentage;

    // Risk breakdown
    private Double eventRisk;
    private Double weatherRisk;
    private Double totalRisk;

    // Weather details
    private String weatherCondition;
    private Double temperature;
    private Double windSpeed;
    private Double humidity;

    private Double premiumAmount;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    private LocalDateTime requestedAt;

    private LocalDateTime approvedAt;


    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    public PolicySubscription() {}

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public Double getRiskPercentage() {
        return riskPercentage;
    }

    public void setRiskPercentage(Double riskPercentage) {
        this.riskPercentage = riskPercentage;
    }

    public Double getEventRisk() {
        return eventRisk;
    }

    public void setEventRisk(Double eventRisk) {
        this.eventRisk = eventRisk;
    }

    public Double getWeatherRisk() {
        return weatherRisk;
    }

    public void setWeatherRisk(Double weatherRisk) {
        this.weatherRisk = weatherRisk;
    }

    public Double getTotalRisk() {
        return totalRisk;
    }

    public void setTotalRisk(Double totalRisk) {
        this.totalRisk = totalRisk;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Double getPremiumAmount() {
        return premiumAmount;
    }

    public void setPremiumAmount(Double premiumAmount) {
        this.premiumAmount = premiumAmount;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public User getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(User approvedBy) {
        this.approvedBy = approvedBy;
    }
}