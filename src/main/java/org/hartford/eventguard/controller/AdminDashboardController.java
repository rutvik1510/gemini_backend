package org.hartford.eventguard.controller;

import org.hartford.eventguard.dto.ApiResponse;
import org.hartford.eventguard.dto.DashboardStatsResponse;
import org.hartford.eventguard.service.AdminUserService;
import org.hartford.eventguard.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    private final DashboardService dashboardService;
    private final AdminUserService adminUserService;

    public AdminDashboardController(DashboardService dashboardService, AdminUserService adminUserService) {
        this.dashboardService = dashboardService;
        this.adminUserService = adminUserService;
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats() {
        DashboardStatsResponse stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success("Dashboard stats fetched successfully", stats));
    }

    @GetMapping("/underwriters")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getUnderwriters() {
        return ResponseEntity.ok(ApiResponse.success("Underwriters fetched", adminUserService.getUnderwriters()));
    }

    @GetMapping("/claims-officers")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getClaimsOfficers() {
        return ResponseEntity.ok(ApiResponse.success("Claims officers fetched", adminUserService.getClaimsOfficers()));
    }
}
