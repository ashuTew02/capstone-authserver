package com.capstone.authServer.controller;

import com.capstone.authServer.dto.UpdateAlertRequest;
import com.capstone.authServer.dto.event.StateUpdateJobEvent;
import com.capstone.authServer.dto.event.payload.StateUpdateJobEventPayload;
import com.capstone.authServer.dto.response.ApiResponse;
import com.capstone.authServer.kafka.producer.StateUpdateJobEventProducer;
import com.capstone.authServer.model.Tool;
import com.capstone.authServer.model.Tenant;
import com.capstone.authServer.repository.TenantRepository;
import com.capstone.authServer.security.annotation.AllowedRoles;
import com.capstone.authServer.service.github.update.GitHubFindingUpdateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/github")
@CrossOrigin
public class GitHubAlertController {

    private final Map<Tool, GitHubFindingUpdateService> serviceByTool;
    private final TenantRepository tenantRepository;
    private final StateUpdateJobEventProducer producer;

    public GitHubAlertController(
            List<GitHubFindingUpdateService> services,
            TenantRepository tenantRepository,
            StateUpdateJobEventProducer producer
    ) {
        // Build a map { CODE_SCAN -> codeScanService, DEPENDABOT -> depService, etc. }
        this.serviceByTool = services.stream()
            .collect(Collectors.toMap(
                GitHubFindingUpdateService::getToolType,
                Function.identity()
            ));
        this.tenantRepository = tenantRepository;
        this.producer = producer;
    }

    @PatchMapping("/alert")
    @AllowedRoles({"SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<?>> updateGithubAlert(@RequestBody UpdateAlertRequest request) {
        // 1) Identify which service to call
        GitHubFindingUpdateService service = serviceByTool.get(request.getTool());
        if (service == null) {
            throw new IllegalArgumentException("Unsupported tool: " + request.getTool());
        }

        // 2) Retrieve tenant info from SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long tenantId = (Long) auth.getDetails(); // we set this in JwtAuthenticationFilter
        Tenant tenant = tenantRepository.findById(tenantId)
            .orElseThrow(() -> new RuntimeException("Tenant not found with ID: " + tenantId));
        String esFindingId = request.getId();
        // 3) Extract needed data from Tenant
        String owner = tenant.getOwner();
        String repo = tenant.getRepo();
        // String pat  = tenant.getPat();  // personal access token
        Tool tool = request.getTool();
        StateUpdateJobEventPayload payload = new StateUpdateJobEventPayload(
            esFindingId,
            tenantId,
            tool,
            owner,
            repo,
            request.getAlertNumber(),
            request.getFindingState()
        );
        StateUpdateJobEvent event = new StateUpdateJobEvent(payload);
        // 4) Call the appropriate service
        producer.produce(event);
        // service.updateFinding(
        //     owner,
        //     repo,
        //     pat,
        //     request.getAlertNumber(),
        //     request.getFindingState(),
        //     request.getId(),
        //     tenantId
        // );

        // 5) Return standard success response
        return new ResponseEntity<>(
            ApiResponse.success(
                HttpStatus.OK.value(),
                "Alert updated successfully.",
                null
            ),
            HttpStatus.OK
        );
    }
}
