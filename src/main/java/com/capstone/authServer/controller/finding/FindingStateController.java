package com.capstone.authServer.controller.finding;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.authServer.dto.response.ApiResponse;
import com.capstone.authServer.model.FindingState;
import com.capstone.authServer.security.annotation.AllowedRoles;

@RestController
@CrossOrigin
public class FindingStateController {

    @GetMapping("/findings/state")
    @AllowedRoles({"USER", "ADMIN","SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<?>> getStates() {
        List<FindingState> states = Arrays.asList(FindingState.values());

        var responseData = new Object() {
            public int count = states.size();
            public List<FindingState> data = states;
        };

        return new ResponseEntity<>(
            ApiResponse.success(HttpStatus.OK.value(), "Finding states fetched successfully.", responseData),
            HttpStatus.OK
        );
    }
}
