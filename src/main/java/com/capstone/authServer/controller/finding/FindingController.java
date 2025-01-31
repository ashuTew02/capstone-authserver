package com.capstone.authServer.controller.finding;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.authServer.dto.FindingResponseDTO;
import com.capstone.authServer.dto.SearchResultDTO;
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

    public Map<String, Object> getFindings(
            @RequestParam(required = false) ScanToolType toolType,
            @RequestParam(required = false) FindingSeverity severity,
            @RequestParam(required = false) FindingState state,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size) {

        // Fetch results from service
        SearchResultDTO<Finding> searchResult = service.searchFindings(toolType, severity, state, page, size);

        // Convert findings to DTO
        List<FindingResponseDTO> dtoList = searchResult.getItems().stream()
                .map(FindingToFindingResponseDTO::convert)
                .collect(Collectors.toList());

        // Build response object
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("page", page);
        response.put("size", size);
        response.put("findingsCount", dtoList.size());
        response.put("totalHits", searchResult.getTotalHits());
        response.put("totalPages", searchResult.getTotalPages());
        response.put("findings", dtoList);

        return response;
    }



}
