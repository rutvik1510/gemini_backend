package org.hartford.eventguard.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "risks")
public class Risk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long riskId;

    private Double eventRiskScore;
    private Double weatherRiskScore;
    private Double totalRiskScore;
    private Double riskPercentage;
    private String riskLevel; // LOW, MEDIUM, HIGH
    
    @Column(length = 1000)
    private String riskFactors; // Comma-separated breakdown: "No Fire NOC (+3.0), Large Crowd (+2.0)"

    public Risk() {}

    // Getters and Setters
    public Long getRiskId() { return riskId; }
    public void setRiskId(Long riskId) { this.riskId = riskId; }

    public Double getEventRiskScore() { return eventRiskScore; }
    public void setEventRiskScore(Double eventRiskScore) { this.eventRiskScore = eventRiskScore; }

    public Double getWeatherRiskScore() { return weatherRiskScore; }
    public void setWeatherRiskScore(Double weatherRiskScore) { this.weatherRiskScore = weatherRiskScore; }

    public Double getTotalRiskScore() { return totalRiskScore; }
    public void setTotalRiskScore(Double totalRiskScore) { this.totalRiskScore = totalRiskScore; }

    public Double getRiskPercentage() { return riskPercentage; }
    public void setRiskPercentage(Double riskPercentage) { this.riskPercentage = riskPercentage; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public String getRiskFactors() { return riskFactors; }
    public void setRiskFactors(String riskFactors) { this.riskFactors = riskFactors; }
}
