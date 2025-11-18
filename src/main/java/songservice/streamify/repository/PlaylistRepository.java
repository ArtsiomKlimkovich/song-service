package songservice.streamify.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import songservice.streamify.entity.Playlist;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, UUID> {
    @NotNull
    @EntityGraph(attributePaths = {"trackIds"})
    Optional<Playlist> findById(@NotNull UUID id);
}
