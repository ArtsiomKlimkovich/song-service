package songservice.streamify.service.album;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import songservice.streamify.dto.album.AlbumDto;
import songservice.streamify.dto.album.CreateAlbumDto;
import songservice.streamify.dto.album.UpdateAlbumDto;
import songservice.streamify.entity.Album;
import songservice.streamify.entity.Track;
import songservice.streamify.mapper.AlbumMapper;
import songservice.streamify.repository.AlbumRepository;
import songservice.streamify.repository.TrackRepository;
import songservice.streamify.utils.MinioService;

import java.util.HashSet;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AlbumServiceImpl implements AlbumService {
    private final AlbumRepository albumRepository;
    private final TrackRepository trackRepository;
    private final MinioService minioService;
    private final AlbumMapper albumMapper;

    @Value("${minio.buckets.artwork}")
    private String artworkBucket;

    @Override
    public void createAlbum(CreateAlbumDto dto) throws FileUploadException {
        String coverUrl = minioService.uploadFile(
                dto.cover(),
                artworkBucket,
                "albums/" + dto.artistId() + "/" + dto.name().replaceAll("[^a-zA-Z0-9-]", "_") + "/cover_"
        );

        Album album = Album.builder()
                .artistId(dto.artistId())
                .name(dto.name())
                .releaseDate(dto.releaseDate())
                .coverUrl(coverUrl)
                .trackIds(new HashSet<>())
                .build();

        albumRepository.save(album);
    }

    @Override
    public AlbumDto getAlbumById(UUID id) {
        return albumRepository.findById(id)
                .map(albumMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Album not found: " + id));
    }

    @Override
    public void updateAlbumById(UUID id, UpdateAlbumDto dto) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Album not found: " + id));
        albumMapper.updateAlbumFromDto(dto, album);
        albumRepository.save(album);
    }

    @Override
    public void updateAlbumCover(UUID albumId, MultipartFile newCover) throws FileUploadException {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Album not found: " + albumId));

        String oldUrl = album.getCoverUrl();
        String newUrl = minioService.uploadFile(newCover, artworkBucket,
                "albums/" + album.getArtistId() + "/" + album.getName().replaceAll("[^a-zA-Z0-9-]", "_") + "/cover_");

        album.setCoverUrl(newUrl);
        albumRepository.save(album);

        if (oldUrl != null) {
            String oldObjectName = minioService.extractObjectNameFromUrl(oldUrl, artworkBucket);
            if (oldObjectName != null) {
                minioService.deleteObject(artworkBucket, oldObjectName);
            }
        }
    }

    @Override
    public void addTrackToAlbum(UUID albumId, UUID trackId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Album not found: " + albumId));

        if (!trackRepository.existsById(trackId)) {
            throw new EntityNotFoundException("Track not found: " + trackId);
        }

        album.getTrackIds().add(trackId);
        albumRepository.save(album);
    }

    @Override
    public void deleteAlbumById(UUID id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Album not found: " + id));

        try {
            if (album.getCoverUrl() != null) {
                String obj = minioService.extractObjectNameFromUrl(album.getCoverUrl(), artworkBucket);
                if (obj != null) minioService.deleteObject(artworkBucket, obj);
            }
        } catch (Exception e) {
            log.warn("Failed to cleanup MinIO objects for album {}", id, e);
        }

        trackRepository.deleteById(id);
    }
}