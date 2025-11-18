package songservice.streamify.service.playlist;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.web.multipart.MultipartFile;
import songservice.streamify.dto.playlist.CreatePlaylistDto;
import songservice.streamify.dto.playlist.PlaylistDto;
import songservice.streamify.dto.playlist.UpdatePlaylistDto;

import java.util.UUID;

public interface PlaylistService {
    void createPlaylist(CreatePlaylistDto dto) throws FileUploadException;
    PlaylistDto getPlaylistById(UUID id);
    void updatePlaylistById(UUID id, UpdatePlaylistDto dto);
    void deletePlaylistById(UUID id);
    void updatePlaylistCover(UUID id, MultipartFile cover) throws FileUploadException;
    void addTrackToPlaylist(UUID playlistId, UUID trackId);
}