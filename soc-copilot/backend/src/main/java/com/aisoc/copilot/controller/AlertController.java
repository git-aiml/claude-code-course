package com.aisoc.copilot.controller;

import com.aisoc.copilot.entity.Alert;
import com.aisoc.copilot.repository.AlertRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AlertController {

    @Autowired
    private AlertRepository alertRepository;

    @GetMapping
    public ResponseEntity<List<Alert>> getAllAlerts() {
        List<Alert> alerts = alertRepository.findAll();
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alert> getAlertById(@PathVariable Long id) {
        Optional<Alert> alert = alertRepository.findById(id);
        return alert.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Alert> createAlert(@Valid @RequestBody Alert alert) {
        Alert savedAlert = alertRepository.save(alert);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAlert);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Alert> updateAlert(@PathVariable Long id, @Valid @RequestBody Alert alertDetails) {
        Optional<Alert> alertOptional = alertRepository.findById(id);

        if (alertOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Alert alert = alertOptional.get();
        alert.setTitle(alertDetails.getTitle());
        alert.setDescription(alertDetails.getDescription());
        alert.setSeverity(alertDetails.getSeverity());
        alert.setStatus(alertDetails.getStatus());
        alert.setSourceIp(alertDetails.getSourceIp());
        alert.setDestinationIp(alertDetails.getDestinationIp());

        Alert updatedAlert = alertRepository.save(alert);
        return ResponseEntity.ok(updatedAlert);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long id) {
        if (!alertRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        alertRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/severity/{severity}")
    public ResponseEntity<List<Alert>> getAlertsBySeverity(@PathVariable String severity) {
        List<Alert> alerts = alertRepository.findBySeverity(severity);
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Alert>> getAlertsByStatus(@PathVariable String status) {
        List<Alert> alerts = alertRepository.findByStatus(status);
        return ResponseEntity.ok(alerts);
    }
}
