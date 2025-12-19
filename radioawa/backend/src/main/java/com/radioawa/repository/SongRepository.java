package com.radioawa.repository;

import com.radioawa.entity.Song;
import com.radioawa.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    /**
     * Find song by station, artist, and title (station-scoped)
     */
    Optional<Song> findByStationAndArtistAndTitle(Station station, String artist, String title);

    /**
     * Find all songs for a specific station
     */
    List<Song> findByStation(Station station);

    /**
     * Count songs for a specific station
     */
    long countByStation(Station station);
}
