package com.capstone.authServer.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.authServer.dto.response.ApiResponse;
import com.capstone.authServer.model.ScanToolType;
import com.capstone.authServer.security.annotation.AllowedRoles;

@RestController
@CrossOrigin
public class ToolController {

    @GetMapping("/tool")
    @AllowedRoles({"ADMIN","SUPER_ADMIN","USER"})
    public ResponseEntity<ApiResponse<?>> getTools() {
        List<ScanToolType> tools = Arrays.asList(ScanToolType.values());

        var responseData = new Object() {
            public int count = tools.size();
            public List<ScanToolType> data = tools;
        };

        return new ResponseEntity<>(
            ApiResponse.success(HttpStatus.OK.value(), "Scan tool types fetched successfully.", responseData),
            HttpStatus.OK
        );
    }
}
