package songservice.streamify.dto.track;

import java.util.UUID;

public record TrackDto(
        UUID artistId, // id and artist name will be fetched automatically later
        String name,
        String artistName,
        String artworkUrl,
        String trackUrl
){}