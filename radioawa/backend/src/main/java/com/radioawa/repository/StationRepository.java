package com.radioawa.repository;

import com.radioawa.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {

    /**
     * Find station by code (e.g., "ENGLISH", "HINDI")
     */
    Optional<Station> findByCode(String code);

    /**
     * Find all active stations ordered by display order
     */
    List<Station> findByIsActiveTrueOrderByDisplayOrder();

    /**
     * Find all stations ordered by display order
     */
    List<Station> findAllByOrderByDisplayOrder();
}
