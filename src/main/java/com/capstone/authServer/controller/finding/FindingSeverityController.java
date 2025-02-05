package com.capstone.authServer.controller.finding;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.authServer.dto.response.ApiResponse;
import com.capstone.authServer.model.FindingSeverity;

@RestController
@CrossOrigin
public class FindingSeverityController {

    @GetMapping("/findings/severity")
    public ResponseEntity<ApiResponse<?>> getSeverities() {
        List<FindingSeverity> severities = Arrays.asList(FindingSeverity.values());

        var responseData = new Object() {
            public int count = severities.size();
            public List<FindingSeverity> data = severities;
        };

        return new ResponseEntity<>(
            ApiResponse.success(HttpStatus.OK.value(), "Finding severities fetched successfully.", responseData),
            HttpStatus.OK
        );
    }
}
