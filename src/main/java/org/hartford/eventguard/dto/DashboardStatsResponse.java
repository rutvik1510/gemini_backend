package org.hartford.eventguard.dto;

public class DashboardStatsResponse {

    private Long totalPolicies;
    private Long activePolicies;
    private Long totalEvents;
    private Long totalUsers;
    private Long pendingSubscriptions;
    private Long pendingClaims;
    private Long settledClaims;
    private Double totalRevenue;
    private Double totalPayouts;

    public DashboardStatsResponse() {
    }

    public Long getTotalPolicies() {
        return totalPolicies;
    }

    public void setTotalPolicies(Long totalPolicies) {
        this.totalPolicies = totalPolicies;
    }

    public Long getActivePolicies() {
        return activePolicies;
    }

    public void setActivePolicies(Long activePolicies) {
        this.activePolicies = activePolicies;
    }

    public Long getTotalEvents() {
        return totalEvents;
    }

    public void setTotalEvents(Long totalEvents) {
        this.totalEvents = totalEvents;
    }

    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Long getPendingSubscriptions() {
        return pendingSubscriptions;
    }

    public void setPendingSubscriptions(Long pendingSubscriptions) {
        this.pendingSubscriptions = pendingSubscriptions;
    }

    public Long getPendingClaims() {
        return pendingClaims;
    }

    public void setPendingClaims(Long pendingClaims) {
        this.pendingClaims = pendingClaims;
    }

    public Long getSettledClaims() {
        return settledClaims;
    }

    public void setSettledClaims(Long settledClaims) {
        this.settledClaims = settledClaims;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Double getTotalPayouts() {
        return totalPayouts;
    }

    public void setTotalPayouts(Double totalPayouts) {
        this.totalPayouts = totalPayouts;
    }
}
