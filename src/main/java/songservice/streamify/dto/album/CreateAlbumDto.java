package songservice.streamify.dto.album;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

public record CreateAlbumDto(
   UUID artistId,
   String name,
   LocalDate releaseDate,
   MultipartFile cover
) {}