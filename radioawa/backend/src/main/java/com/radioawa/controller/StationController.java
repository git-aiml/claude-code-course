package com.radioawa.controller;

import com.radioawa.dto.StationResponse;
import com.radioawa.entity.Station;
import com.radioawa.repository.StationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stations")
public class StationController {

    private final StationRepository stationRepository;

    public StationController(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    /**
     * Get all active stations
     * GET /api/stations
     */
    @GetMapping
    public ResponseEntity<List<StationResponse>> getActiveStations() {
        List<Station> stations = stationRepository.findByIsActiveTrueOrderByDisplayOrder();

        List<StationResponse> responses = stations.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /**
     * Get station by code
     * GET /api/stations/{code}
     */
    @GetMapping("/{code}")
    public ResponseEntity<StationResponse> getStationByCode(@PathVariable String code) {
        return stationRepository.findByCode(code)
                .map(station -> ResponseEntity.ok(toResponse(station)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Get all stations (including inactive)
     * GET /api/stations/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<StationResponse>> getAllStations() {
        List<Station> stations = stationRepository.findAllByOrderByDisplayOrder();

        List<StationResponse> responses = stations.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /**
     * Convert Station entity to StationResponse DTO
     */
    private StationResponse toResponse(Station station) {
        return new StationResponse(
                station.getId(),
                station.getCode(),
                station.getName(),
                station.getStreamUrl(),
                station.getMetadataUrl(),
                station.getIsActive(),
                station.getDisplayOrder()
        );
    }
}
