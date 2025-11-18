package songservice.streamify.service.album;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.web.multipart.MultipartFile;
import songservice.streamify.dto.album.AlbumDto;
import songservice.streamify.dto.album.CreateAlbumDto;
import songservice.streamify.dto.album.UpdateAlbumDto;
import songservice.streamify.entity.Album;

import java.util.UUID;

public interface AlbumService {
    void createAlbum(CreateAlbumDto dto) throws FileUploadException;
    AlbumDto getAlbumById(UUID id);
    void updateAlbumById(UUID id, UpdateAlbumDto dto);
    void deleteAlbumById(UUID id);
    void updateAlbumCover(UUID id, MultipartFile cover) throws FileUploadException;
    void addTrackToAlbum(UUID albumId, UUID id);
}
