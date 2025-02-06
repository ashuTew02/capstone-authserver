package com.capstone.authServer.controller;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.authServer.dto.UpdateAlertRequest;
import com.capstone.authServer.dto.response.ApiResponse;
import com.capstone.authServer.model.ScanToolType;
import com.capstone.authServer.service.github.update.GitHubFindingUpdateService;

@RestController
@RequestMapping("/api/github")
@CrossOrigin
public class GitHubAlertController {

    private final Map<ScanToolType, GitHubFindingUpdateService> serviceByTool;

    public GitHubAlertController(List<GitHubFindingUpdateService> services) {
        // Build a map { CODE_SCAN -> codeScanService, DEPENDABOT -> depService, etc. }
        this.serviceByTool = services.stream()
            .collect(Collectors.toMap(
                GitHubFindingUpdateService::getToolType, 
                Function.identity()
            ));
    }

    @PatchMapping("/alert")
    public ResponseEntity<ApiResponse<?>> updateGithubAlert(@RequestBody UpdateAlertRequest request) {
        // 1) Validate or throw
        GitHubFindingUpdateService service = serviceByTool.get(request.getTool());
        if (service == null) {
            throw new IllegalArgumentException("Unsupported tool: " + request.getTool());
        }

        // 2) Hardcode owner/repo for now
        String owner = "ashuTew01";
        String repo = "juice-shop";

        // 3) Call the update logic
        service.updateFinding(owner, repo, request.getAlertNumber(), request.getFindingState(), request.getId());
        // 4) Return a response, same pattern as other endpoints
        return new ResponseEntity<>(
            ApiResponse.success(
                HttpStatus.OK.value(),
                "Alert updated successfully.",
                null // Or some data/DTO
            ),
            HttpStatus.OK
        );
    }

    // Optional: handle errors consistently
    // @ExceptionHandler(RuntimeException.class)
    // public ResponseEntity<ApiResponse<?>> handleRuntimeException(RuntimeException ex) {
    //     return ResponseEntity.badRequest().body(ApiResponse.error(
    //         HttpStatus.BAD_REQUEST.value(),
    //         ex.getMessage()
    //     ));
    // }
}
