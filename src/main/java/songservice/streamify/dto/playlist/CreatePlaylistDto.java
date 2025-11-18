package songservice.streamify.dto.playlist;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record CreatePlaylistDto(
    UUID userId,
    String name,
    MultipartFile cover
){}