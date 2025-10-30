package songservice.streamify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import songservice.streamify.entity.Track;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {
    Optional<Track> findById(UUID id);
}