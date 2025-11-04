package songservice.streamify.dto.track;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record CreateTrackDto(
   UUID artistId, // id and artist name will be fetched automatically later
   String name,
   String artistName,
   MultipartFile artwork,
   MultipartFile file
) {}