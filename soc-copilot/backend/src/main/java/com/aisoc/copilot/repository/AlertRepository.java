package com.aisoc.copilot.repository;

import com.aisoc.copilot.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findBySeverity(String severity);

    List<Alert> findByStatus(String status);

    List<Alert> findBySourceIp(String sourceIp);
}
