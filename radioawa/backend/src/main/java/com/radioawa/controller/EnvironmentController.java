package com.radioawa.controller;

import com.radioawa.dto.EnvironmentInfoResponse;
import com.radioawa.service.EnvironmentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EnvironmentController {

    private final EnvironmentService environmentService;

    public EnvironmentController(EnvironmentService environmentService) {
        this.environmentService = environmentService;
    }

    @GetMapping("/environment")
    public EnvironmentInfoResponse getEnvironmentInfo() {
        return environmentService.getEnvironmentInfo();
    }
}