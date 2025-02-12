package com.capstone.authServer.controller;

import com.capstone.authServer.dto.event.ScanRequestEvent;
import com.capstone.authServer.dto.scan.request.ScanRequestDTO;
import com.capstone.authServer.dto.response.ApiResponse;
import com.capstone.authServer.kafka.producer.ScanRequestEventProducer;
import com.capstone.authServer.model.Tenant;
import com.capstone.authServer.repository.TenantRepository;
import com.capstone.authServer.security.annotation.AllowedRoles;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/scan")
@CrossOrigin
public class ScanController {

    private final ScanRequestEventProducer producer;
    private final TenantRepository tenantRepository;

    public ScanController(ScanRequestEventProducer producer,
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

        // 3) Build a new event with the DB’s owner & repo
        ScanRequestEvent event = new ScanRequestEvent(
            tenant.getOwner(),       // from DB
            tenant.getRepo(),        // from DB
            request.getScanTypes(),
            tenantId   // from client’s request
        );

        // 4) Publish to Kafka
        producer.produce(event);

        // 5) Return success
        return new ResponseEntity<>(
            ApiResponse.success(HttpStatus.OK.value(), "Scan Request Event published successfully!", null),
            HttpStatus.OK
        );
    }
}
