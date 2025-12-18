package com.radioawa.repository;

import com.radioawa.entity.Rating;
import com.radioawa.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findBySongAndUserId(Song song, String userId);
    boolean existsBySongAndUserId(Song song, String userId);

    // IP-based rate limiting queries
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.ipAddress = :ipAddress AND r.createdAt > :since")
    long countByIpAddressAndCreatedAtAfter(@Param("ipAddress") String ipAddress, @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.ipAddress = :ipAddress AND r.song = :song")
    long countByIpAddressAndSong(@Param("ipAddress") String ipAddress, @Param("song") Song song);
}
