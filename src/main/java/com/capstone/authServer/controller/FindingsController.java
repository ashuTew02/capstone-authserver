package com.capstone.authServer.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.authServer.model.Finding;
import com.capstone.authServer.model.FindingSeverity;
import com.capstone.authServer.model.FindingState;
import com.capstone.authServer.model.ScanToolType;
import com.capstone.authServer.service.ElasticSearchService;

@RestController
public class FindingsController {

    private final ElasticSearchService service;

    public FindingsController(ElasticSearchService service) {
        this.service = service;
    }

    @GetMapping("/findings")
    public Map<String, Object> getFindings(
            @RequestParam(required = false) ScanToolType toolType,
            @RequestParam(required = false) FindingSeverity severity,
            @RequestParam(required = false) FindingState state,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size) {
        List<Finding> findings = service.searchFindings(toolType, severity, state, page, size);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("findingsCount", findings.size());
        response.put("findings", findings);
        return response;
    }
}
