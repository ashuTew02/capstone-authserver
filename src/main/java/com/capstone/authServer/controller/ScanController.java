package com.capstone.authServer.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.authServer.dto.event.ScanRequestEvent;
import com.capstone.authServer.kafka.producer.ScanRequestEventProducer;


@RestController
@RequestMapping("/scan")
public class ScanController {

    private final ScanRequestEventProducer producer;

    public ScanController(ScanRequestEventProducer producer) {
        this.producer = producer;
    }

    @PostMapping("/request")
    public Map<String, Object> createScanRequest(@RequestBody ScanRequestEvent scanRequestEvent) {
        Map<String, Object> response = new LinkedHashMap<>();
        try{
            producer.produce(scanRequestEvent);
            response.put("status", "success");
            response.put("message", "Scan Request Event published successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "failure");
            response.put("message", "Failed to publish Scan Request Event!");
        }
        return response;
    }
}
