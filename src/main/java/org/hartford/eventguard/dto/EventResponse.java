package org.hartford.eventguard.dto;

import org.hartford.eventguard.entity.EventDomain;
import java.time.LocalDate;

public class EventResponse {

    private Long eventId;
    private Long subscriptionId;
    private String eventName;
    private EventDomain eventType;
    private String customerName;
    private String location;
    private LocalDate eventDate;
    private Double budget;
    private Integer numberOfAttendees;
    private Integer durationInDays;
    private String status;
    private Boolean isPremiumPaid;
    private Boolean hasClaim;
    private String claimStatus;

    public EventResponse() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsPremiumPaid() {
        return isPremiumPaid;
    }

    public void setIsPremiumPaid(Boolean isPremiumPaid) {
        this.isPremiumPaid = isPremiumPaid;
    }

    public Boolean getHasClaim() {
        return hasClaim;
    }

    public void setHasClaim(Boolean hasClaim) {
        this.hasClaim = hasClaim;
    }

    public String getClaimStatus() {
        return claimStatus;
    }

    public void setClaimStatus(String claimStatus) {
        this.claimStatus = claimStatus;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
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

    public EventDomain getEventType() {
        return eventType;
    }

    public void setEventType(EventDomain eventType) {
        this.eventType = eventType;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public Integer getNumberOfAttendees() {
        return numberOfAttendees;
    }

    public void setNumberOfAttendees(Integer numberOfAttendees) {
        this.numberOfAttendees = numberOfAttendees;
    }

    public Integer getDurationInDays() {
        return durationInDays;
    }

    public void setDurationInDays(Integer durationInDays) {
        this.durationInDays = durationInDays;
    }
}
