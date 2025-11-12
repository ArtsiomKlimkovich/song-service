package songservice.streamify.service.album;

import org.springframework.web.multipart.MultipartFile;
import songservice.streamify.dto.album.AlbumDto;
import songservice.streamify.dto.album.CreateAlbumDto;
import songservice.streamify.dto.album.UpdateAlbumDto;

import java.util.List;
import java.util.UUID;

public interface AlbumService {
    void createAlbum(CreateAlbumDto dto);
    AlbumDto getAlbumById(UUID id);
    void updateAlbumById(UpdateAlbumDto dto, UUID id);
    void updateAlbumCover(UUID id, MultipartFile coverFile);
    void addTracks(UUID albumId, List<MultipartFile> files);
    void addTrackIds(UUID albumId, List<UUID> trackIds);
}
