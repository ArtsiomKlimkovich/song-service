package songservice.streamify.dto.album;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

public record AlbumDto(
    UUID artistId,
    String name,
    Date releaseDate,
    String coverUrl,
    Set<UUID> trackIds
){}