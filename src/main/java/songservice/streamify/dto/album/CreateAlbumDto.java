package songservice.streamify.dto.album;

import java.util.Date;
import java.util.UUID;

public record CreateAlbumDto(
   UUID artistId,
   String name,
   Date releaseDate,
   String coverUrl
) {}