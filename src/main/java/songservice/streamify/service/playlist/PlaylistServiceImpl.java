package songservice.streamify.service.playlist;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import songservice.streamify.dto.playlist.CreatePlaylistDto;
import songservice.streamify.dto.playlist.PlaylistDto;
import songservice.streamify.dto.playlist.UpdatePlaylistDto;
import songservice.streamify.entity.Playlist;
import songservice.streamify.mapper.PlaylistMapper;
import songservice.streamify.repository.PlaylistRepository;
import songservice.streamify.repository.TrackRepository;
import songservice.streamify.utils.MinioService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PlaylistServiceImpl implements PlaylistService{
    private final PlaylistRepository playlistRepository;
    private final TrackRepository trackRepository;
    private final MinioService minioService;
    private final PlaylistMapper playlistMapper;

    @Value("${minio.buckets.artwork}")
    private String artworkBucket;

    @Override
    public void createPlaylist(CreatePlaylistDto dto) throws FileUploadException {
        String coverUrl = minioService.uploadFile(
                dto.cover(),
                artworkBucket,
                "playlists/" + dto.userId() + "/" + dto.name().replaceAll("[^a-zA-Z0-9-]", "_") + "/cover_"
        );

        Playlist playlist = Playlist.builder()
                .userId(dto.userId())
                .name(dto.name())
                .coverUrl(coverUrl)
                .trackIds(new HashMap<>())
                .build();

        playlistRepository.save(playlist);
    }

    @Override
    public PlaylistDto getPlaylistById(UUID id) {
        return playlistRepository.findById(id)
                .map(playlistMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Playlist not found: " + id));
    }

    @Override
    public void updatePlaylistById(UUID id, UpdatePlaylistDto dto) {
        Playlist playlist = playlistRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Playlist not found."));
        playlistMapper.updatePlaylistFromDto(dto, playlist);
        playlistRepository.save(playlist);
    }

    @Override
    public void deletePlaylistById(UUID id) {
        Playlist playlist = playlistRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Playlist not found."));

        try{
            if(playlist.getCoverUrl() != null){
                String obj = minioService.extractObjectNameFromUrl(playlist.getCoverUrl(), artworkBucket);
                if(obj != null){
                    minioService.deleteObject(artworkBucket, obj);
                }
            }
        }catch (Exception e) {
            log.warn("Failed to cleanup MinIO objects for album {}", id, e);
        }

        playlistRepository.delete(playlist);
    }

    @Override
    public void updatePlaylistCover(UUID id, MultipartFile cover) throws FileUploadException {
        Playlist playlist = playlistRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Playlist not found."));

        String oldUrl = playlist.getCoverUrl();
        String newUrl = minioService.uploadFile(cover, artworkBucket,
                "playlists/" + playlist.getUserId() + "/" + playlist.getName().replaceAll("[^a-zA-Z0-9-]", "_") + "/cover_");

        playlist.setCoverUrl(newUrl);
        playlistRepository.save(playlist);

        if(oldUrl != null){
            String oldObjectName = minioService.extractObjectNameFromUrl(oldUrl, artworkBucket);
            if(oldObjectName != null){
                minioService.deleteObject(artworkBucket, oldObjectName);
            }
        }
    }

    @Override
    public void addTrackToPlaylist(UUID playlistId, UUID trackId) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(() -> new EntityNotFoundException("Playlist not found."));
        if(!trackRepository.existsById(trackId)){
            throw new EntityNotFoundException("Track not found.");
        }

        playlist.getTrackIds().put(trackId, LocalDateTime.now());
        playlistRepository.save(playlist);
    }
}