package com.capstone.authServer.controller.runbook;

import com.capstone.authServer.dto.response.ApiResponse;
import com.capstone.authServer.dto.runbook.*;
import com.capstone.authServer.model.runbook.*;
import com.capstone.authServer.security.annotation.AllowedRoles;
import com.capstone.authServer.service.runbook.RunbookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/runbooks")
@CrossOrigin
public class RunbookController {

    private final RunbookService runbookService;

    public RunbookController(RunbookService runbookService) {
        this.runbookService = runbookService;
    }

    // 1) CREATE
    @PostMapping
    @AllowedRoles({"USER", "ADMIN","SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<Runbook>> createRunbook(@RequestBody CreateRunbookRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!StringUtils.hasText(auth.getName())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Not logged in"));
        }
        Long tenantId = (Long) auth.getDetails();

        Runbook runbook = runbookService.createRunbook(request, tenantId);
        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Runbook created successfully",
                        runbook
                )
        );
    }

    // 2) GET all runbooks (for tenant)
    @GetMapping
    @AllowedRoles({"USER", "ADMIN","SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<List<Runbook>>> getTenantRunbooks() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long tenantId = (Long) auth.getDetails();

        List<Runbook> runbooks = runbookService.getRunbooksForTenant(tenantId);
        return ResponseEntity.ok(
                ApiResponse.success(
                HttpStatus.OK.value(),
                "Runbooks retrieved successfully",
                runbooks
        ));
    }

    // 3) GET single runbook
    @GetMapping("/{id}/detail")
    @AllowedRoles({"USER", "ADMIN","SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<Runbook>> getRunbookById(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long tenantId = (Long) auth.getDetails();

        Runbook runbook = runbookService.getRunbookById(id, tenantId);
        if (runbook == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Runbook not found"));
        }

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Runbook retrieved successfully",
                        runbook
                )
        );
    }

    // 4) Check runbook status
    @GetMapping("/{id}/status")
    @AllowedRoles({"USER", "ADMIN","SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<List<String>>> checkRunbookStatus(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long tenantId = (Long) auth.getDetails();

        List<String> status = runbookService.checkRunbookStatus(id, tenantId);
        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Runbook status retrieved",
                        status
                )
        );
    }

    // 5) Configure triggers
    @PostMapping("/{id}/triggers")
    @AllowedRoles({"USER", "ADMIN","SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<List<RunbookTrigger>>> configureTriggers(
            @PathVariable Long id,
            @RequestBody ConfigureTriggersRequest request
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long tenantId = (Long) auth.getDetails();
        System.out.println("INSIDE CONFIGURE TRIGGER CONTROLLER");
        // Overwrite runbookId in request if you'd like:
        request.setRunbookId(id);
        System.out.println("Runbook id: "+ request.getRunbookId());
        System.out.println("Triggers: "+ request.getTriggers().get(0).getTriggerType().toString());
        List<RunbookTrigger> triggers = runbookService.configureTriggers(request, tenantId);
        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Runbook triggers configured successfully",
                        triggers
                )
        );
    }

    // 6) Get runbook triggers
    @GetMapping("/{id}/triggers")
    @AllowedRoles({"USER", "ADMIN","SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<List<RunbookTrigger>>> getRunbookTriggers(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long tenantId = (Long) auth.getDetails();

        List<RunbookTrigger> triggers = runbookService.getRunbookTriggers(id, tenantId);
        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Runbook triggers retrieved successfully",
                        triggers
                )
        );
    }

    // 7) GET all available triggers
    @GetMapping("/triggers/available")
    @AllowedRoles({"USER", "ADMIN","SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<List<String>>> getAllAvailableTriggers() {
        List<String> triggers = runbookService.getAllAvailableTriggers();
        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "List of available triggers",
                        triggers
                )
        );
    }

    // 8) Configure filters
    @PostMapping("/{id}/filters")
    @AllowedRoles({"USER", "ADMIN","SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<List<RunbookFilter>>> configureFilters(
            @PathVariable Long id,
            @RequestBody ConfigureFiltersRequest request
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long tenantId = (Long) auth.getDetails();

        request.setRunbookId(id);

        List<RunbookFilter> filters = runbookService.configureFilters(request, tenantId);
        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Runbook filters configured successfully",
                        filters
                )
        );
    }

    // 9) Get runbook filters
    @GetMapping("/{id}/filters")
    @AllowedRoles({"USER", "ADMIN","SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<List<RunbookFilter>>> getRunbookFilters(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long tenantId = (Long) auth.getDetails();

        List<RunbookFilter> filters = runbookService.getRunbookFilters(id, tenantId);
        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Runbook filters retrieved successfully",
                        filters
                )
        );
    }

    // 10) Configure actions
    @PostMapping("/{id}/actions")
    @AllowedRoles({"USER", "ADMIN","SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<List<RunbookAction>>> configureActions(
            @PathVariable Long id,
            @RequestBody ConfigureActionsRequest request
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long tenantId = (Long) auth.getDetails();

        request.setRunbookId(id);

        List<RunbookAction> actions = runbookService.configureActions(request, tenantId);
        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Runbook actions configured successfully",
                        actions
                )
        );
    }

    // 11) Get runbook actions
    @GetMapping("/{id}/actions")
    @AllowedRoles({"USER", "ADMIN","SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<List<RunbookAction>>> getRunbookActions(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long tenantId = (Long) auth.getDetails();

        List<RunbookAction> actions = runbookService.getRunbookActions(id, tenantId);
        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Runbook actions retrieved successfully",
                        actions
                )
        );
    }

    // 12) Enable/Disable runbook
    @PutMapping("/{id}/enabled")
    @AllowedRoles({"USER", "ADMIN","SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<Runbook>> updateRunbookEnabled(
            @PathVariable Long id,
            @RequestParam("enabled") boolean enabled
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long tenantId = (Long) auth.getDetails();

        Runbook updated = runbookService.updateRunbookEnabled(id, tenantId, enabled);
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Runbook not found"));
        }
        return ResponseEntity.ok(
            ApiResponse.success(
                HttpStatus.OK.value(),
                "Runbook enable/disable updated.",
                updated
            )
        );
    }

    // 13) Delete runbook
    @DeleteMapping("/{id}")
    @AllowedRoles({"USER", "ADMIN","SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<?>> deleteRunbook(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long tenantId = (Long) auth.getDetails();

        boolean success = runbookService.deleteRunbook(id, tenantId);
        if (!success) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Runbook not found"));
        }
        return ResponseEntity.ok(
            ApiResponse.success(HttpStatus.OK.value(), "Runbook deleted successfully.", null)
        );
    }
}
