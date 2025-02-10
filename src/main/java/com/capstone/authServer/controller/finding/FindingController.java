package com.capstone.authServer.controller.finding;

import com.capstone.authServer.dto.FindingResponseDTO;
import com.capstone.authServer.dto.SearchResultDTO;
import com.capstone.authServer.dto.response.ApiResponse;
import com.capstone.authServer.model.Finding;
import com.capstone.authServer.model.FindingSeverity;
import com.capstone.authServer.model.FindingState;
import com.capstone.authServer.model.ScanToolType;
import com.capstone.authServer.security.annotation.AllowedRoles;
import com.capstone.authServer.service.ElasticSearchService;
import com.capstone.authServer.utils.FindingToFindingResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin

public class FindingController {

    private final ElasticSearchService service;

    public FindingController(ElasticSearchService service) {
        this.service = service;
    }

    @GetMapping("/findings")
    @AllowedRoles({"USER", "ADMIN","SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<?>> getFindings(
            // Changed each of these to List<T> to allow multiple
            @RequestParam(required = false) List<ScanToolType> toolType,
            @RequestParam(required = false) List<FindingSeverity> severity,
            @RequestParam(required = false) List<FindingState> state,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size
    ) {

        /*
          If no query params are passed for these, they will be null or empty,
          and we simply won't filter by that field.
          e.g. /findings?severity=HIGH&severity=LOW => severity = [HIGH, LOW]
        */

        SearchResultDTO<Finding> searchResult = service.searchFindings(
                toolType,
                severity,
                state,
                page,
                size
        );

        // Convert findings to DTO
        List<FindingResponseDTO> dtoList = searchResult.getItems().stream()
                .map(FindingToFindingResponseDTO::convert)
                .collect(Collectors.toList());

        // Wrap your response in an object or custom class for clarity
        var responseData = new Object() {
            public int currentPage = page;
            public int pageSize = size;
            public int findingsCount = dtoList.size();
            public long totalHits = searchResult.getTotalHits();
            public int totalPages = searchResult.getTotalPages();
            public List<FindingResponseDTO> findings = dtoList;
        };

        return new ResponseEntity<>(
            ApiResponse.success(HttpStatus.OK.value(), "Findings fetched successfully.", responseData),
            HttpStatus.OK
        );
    }

    @GetMapping("finding")
    @AllowedRoles({"USER", "ADMIN","SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<?>> getFindingById(@RequestParam String id) {
        Finding finding = service.getFindingById(id);
        FindingResponseDTO dto = FindingToFindingResponseDTO.convert(finding);
        return new ResponseEntity<>(
            ApiResponse.success(HttpStatus.OK.value(), "Finding fetched successfully.", dto),
            HttpStatus.OK
        );
    }
}
