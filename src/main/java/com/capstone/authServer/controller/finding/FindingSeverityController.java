package com.capstone.authServer.controller.finding;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.authServer.model.FindingSeverity;


@RestController
@CrossOrigin
public class FindingSeverityController {
    
    @GetMapping("/findings/severity")
    public Map<String, Object> getSeverities() {
        List<FindingSeverity> severities = Arrays.asList(FindingSeverity.values());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("count", severities.size());
        response.put("data", severities);
        return response;
        
    }
    
}
