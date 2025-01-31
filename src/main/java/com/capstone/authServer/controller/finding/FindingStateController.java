package com.capstone.authServer.controller.finding;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.authServer.model.FindingState;


@RestController
@CrossOrigin
public class FindingStateController {

    @GetMapping("/findings/state")
    public Map<String, Object> getStates() {
        List<FindingState> states = Arrays.asList(FindingState.values());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("count", states.size());
        response.put("data", states);
        return response;
        
    }
    
}
