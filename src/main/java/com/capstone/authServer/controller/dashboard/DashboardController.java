package com.capstone.authServer.controller.dashboard;

import com.capstone.authServer.dto.response.ApiResponse;
import com.capstone.authServer.service.ElasticSearchService;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/dashboard")
public class DashboardController {

    private final ElasticSearchService service;
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    public DashboardController(ElasticSearchService service) {
        this.service = service;
    }

    @GetMapping("/toolDistribution")
    public ResponseEntity<?> getToolDistribution() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long tenantId = (Long) auth.getDetails(); // we set this in JwtAuthenticationFilter
        logger.info(tenantId.toString());
        Map<String, Long> dist = service.getToolDistribution(tenantId);
        return ResponseEntity.ok(
            ApiResponse.success(
                HttpStatus.OK.value(),
                "Tool distribution fetched successfully",
                dist
            )
        );
    }

    @GetMapping("/severityDistribution")
    public ResponseEntity<?> getSeverityDistribution(
        @RequestParam(required = false) List<String> tool
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long tenantId = (Long) auth.getDetails(); // we set this in JwtAuthenticationFilter
        Map<String, Long> dist = service.getSeverityDistribution(tool, tenantId);
        return ResponseEntity.ok(
            ApiResponse.success(
                HttpStatus.OK.value(),
                "Severity distribution fetched successfully",
                dist
            )
        );
    }

    @GetMapping("/stateDistribution")
    public ResponseEntity<?> getStateDistribution(
        @RequestParam(required = false) List<String> tool
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long tenantId = (Long) auth.getDetails(); // we set this in JwtAuthenticationFilter

        Map<String, Long> dist = service.getStateDistribution(tool, tenantId);
        return ResponseEntity.ok(
            ApiResponse.success(
                HttpStatus.OK.value(),
                "State distribution fetched successfully",
                dist
            )
        );
    }

    @GetMapping("/cvssDistribution")
    public ResponseEntity<?> getCvssDistribution(
        @RequestParam(required = false) List<String> tool
    ) throws ElasticsearchException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long tenantId = (Long) auth.getDetails(); // we set this in JwtAuthenticationFilter

        List<Map<String, Object>> dist = service.getCvssDistribution(tool, tenantId);
        return ResponseEntity.ok(
            ApiResponse.success(
                HttpStatus.OK.value(),
                "CVSS distribution fetched successfully",
                dist
            )
        );
    }
}
