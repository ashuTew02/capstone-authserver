package com.capstone.authServer.controller;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.authServer.model.ScanToolType;


@RestController
@CrossOrigin
public class ToolController {
    
    @GetMapping("/tool")
    public Map<String, Object> getTools() {
        List<ScanToolType> tools = Arrays.asList(ScanToolType.values());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("count", tools.size());
        response.put("data", tools);
        return response;
        
    }
    
}
