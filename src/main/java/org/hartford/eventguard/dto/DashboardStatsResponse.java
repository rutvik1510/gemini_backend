package org.hartford.eventguard.dto;

public class DashboardStatsResponse {

    private Long totalPolicies;
    private Long activePolicies;
    private Long totalEvents;
    private Long pendingSubscriptions;
    private Long pendingClaims;
    private Double totalRevenue;
    private Double totalPayouts;
    private Double netProfit;

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

    public Double getNetProfit() {
        return netProfit;
    }

    public void setNetProfit(Double netProfit) {
        this.netProfit = netProfit;
    }
}
