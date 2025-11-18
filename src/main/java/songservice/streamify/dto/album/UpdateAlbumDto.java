package songservice.streamify.dto.album;

import java.util.Date;

public record UpdateAlbumDto(
    String name,
    Date releaseDate
){}