package songservice.streamify.dto.playlist;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

public record PlaylistDto(
    UUID userId,
    String name,
    @CreationTimestamp LocalDateTime creationDate,
    String coverUrl,
    HashMap<UUID, LocalDateTime> trackIds
){}