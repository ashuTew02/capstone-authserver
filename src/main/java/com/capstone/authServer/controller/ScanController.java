package com.capstone.authServer.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.authServer.dto.event.ScanRequestEvent;
import com.capstone.authServer.dto.response.ApiResponse;
import com.capstone.authServer.kafka.producer.ScanRequestEventProducer;

@RestController
@CrossOrigin
@RequestMapping("/scan")
public class ScanController {

    private final ScanRequestEventProducer producer;

    public ScanController(ScanRequestEventProducer producer) {
        this.producer = producer;
    }

    @PostMapping("/request")
    public ResponseEntity<ApiResponse<?>> createScanRequest(@Valid @RequestBody ScanRequestEvent scanRequestEvent) {
        // If validation fails, it will throw MethodArgumentNotValidException (handled in GlobalExceptionHandler).

        producer.produce(scanRequestEvent);
        return new ResponseEntity<>(
            ApiResponse.success(HttpStatus.OK.value(), "Scan Request Event published successfully!", null),
            HttpStatus.OK
        );
    }
}
