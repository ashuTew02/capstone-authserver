package com.capstone.authServer.controller.finding;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.authServer.dto.FindingResponseDTO;
import com.capstone.authServer.dto.SearchResultDTO;
import com.capstone.authServer.dto.response.ApiResponse;
import com.capstone.authServer.model.Finding;
import com.capstone.authServer.model.FindingSeverity;
import com.capstone.authServer.model.FindingState;
import com.capstone.authServer.model.ScanToolType;
import com.capstone.authServer.service.ElasticSearchService;
import com.capstone.authServer.utils.FindingToFindingResponseDTO;


@RestController
@CrossOrigin
public class FindingController {

    private final ElasticSearchService service;

    public FindingController(ElasticSearchService service) {
        this.service = service;
    }

    @GetMapping("/findings")
    public ResponseEntity<ApiResponse<?>> getFindings(
            @RequestParam(required = false) ScanToolType toolType,
            @RequestParam(required = false) FindingSeverity severity,
            @RequestParam(required = false) FindingState state,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size) {

        // If invalid enum values are passed, MethodArgumentTypeMismatchException is thrown
        // and handled by GlobalExceptionHandler.

        SearchResultDTO<Finding> searchResult = service.searchFindings(toolType, severity, state, page, size);

        // Convert findings to DTO
        List<FindingResponseDTO> dtoList = searchResult.getItems().stream()
                .map(FindingToFindingResponseDTO::convert)
                .collect(Collectors.toList());

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
    public ResponseEntity<ApiResponse<?>> getFindingById(@RequestParam String id) {
        Finding finding = service.getFindingById(id);
        FindingResponseDTO dto = FindingToFindingResponseDTO.convert(finding);
        return new ResponseEntity<>(ApiResponse.success(HttpStatus.OK.value(), "Finding fetched successfully.", dto), HttpStatus.OK);
    }

    
    
}
