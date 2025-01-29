package com.capstone.authServer.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.authServer.dto.event.ScanRequestEvent;
import com.capstone.authServer.kafka.producer.ScanRequestEventProducer;

/**
 * REST controller to trigger the publishing of a scan event.
 */
@RestController
@RequestMapping("/scan")
public class ScanController {

    private final ScanRequestEventProducer producer;

    public ScanController(ScanRequestEventProducer producer) {
        this.producer = producer;
    }

    @PostMapping("/publish")
    public String publishScan(@RequestBody ScanRequestEvent scanRequestEvent) {
        producer.produce(scanRequestEvent);
        return "Scan Request Event published successfully!";
    }
}
