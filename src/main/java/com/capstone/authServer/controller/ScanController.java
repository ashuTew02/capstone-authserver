package com.capstone.authServer.controller;

import com.capstone.authServer.dto.event.ScanRequestJobEvent;
import com.capstone.authServer.dto.event.payload.ScanRequestJobEventPayload;
import com.capstone.authServer.dto.scan.request.ScanRequestDTO;
import com.capstone.authServer.dto.response.ApiResponse;
import com.capstone.authServer.kafka.producer.ScanRequestJobEventProducer;
import com.capstone.authServer.model.Tenant;
import com.capstone.authServer.model.Tool;
import com.capstone.authServer.repository.TenantRepository;
import com.capstone.authServer.security.annotation.AllowedRoles;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

@RestController
@RequestMapping("/scan")
@CrossOrigin
public class ScanController {

    private final ScanRequestJobEventProducer producer;
    private final TenantRepository tenantRepository;

    public ScanController(ScanRequestJobEventProducer producer,
                          TenantRepository tenantRepository) {
        this.producer = producer;
        this.tenantRepository = tenantRepository;
    }

    @PostMapping("/request")
    @AllowedRoles({"ADMIN", "SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<?>> createScanRequest(@Valid @RequestBody ScanRequestDTO request) {
        // 1) Get tenantId from SecurityContext (we stored it in .setDetails(...) in JwtAuthenticationFilter)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long tenantId = (Long) auth.getDetails(); // cast from Object

        // 2) Lookup the Tenant in DB
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found with ID: " + tenantId));

        List<Tool> toolsToBeScanned;        
        if(request.getScanAll() || request.getToolsToScan() == null || request.getToolsToScan().isEmpty()) {
            toolsToBeScanned = Arrays.asList(Tool.values());
        } else {
            toolsToBeScanned = request.getToolsToScan();
        }

        for (Tool tool : toolsToBeScanned) {
            ScanRequestJobEventPayload payload = new ScanRequestJobEventPayload(
                tool,
                tenantId,
                tenant.getOwner(),
                tenant.getRepo()
            );
            ScanRequestJobEvent event = new ScanRequestJobEvent(payload);
            producer.produce(event);
        }
        

        return new ResponseEntity<>(
            ApiResponse.success(HttpStatus.OK.value(), "Scan Request Event published successfully!", null),
            HttpStatus.OK
        );
    }
}
