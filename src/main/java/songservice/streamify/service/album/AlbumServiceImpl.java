package songservice.streamify.service.album;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import songservice.streamify.dto.album.AlbumDto;
import songservice.streamify.dto.album.CreateAlbumDto;
import songservice.streamify.dto.album.UpdateAlbumDto;
import songservice.streamify.entity.Album;
import songservice.streamify.entity.Track;
import songservice.streamify.repository.AlbumRepository;
import songservice.streamify.repository.TrackRepository;
import songservice.streamify.utils.MinioUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final TrackRepository trackRepository;
    private final MinioUtils minioUtils;

    @Value("${minio.buckets.artwork}")
    private String artworkBucket;

    @Value("${minio.buckets.track}")
    private String trackBucket;

    @Override
    @SneakyThrows
    public void createAlbum(CreateAlbumDto dto) {
        Album album = new Album();
        album.setArtistId(dto.artistId());
        album.setName(dto.name());
        album.setReleaseDate(dto.releaseDate());
        album.setCoverUrl(dto.coverUrl());
        albumRepository.save(album);
    }

    @Override
    public AlbumDto getAlbumById(UUID id) {
        Album album = albumRepository.findById(id).orElseThrow(() -> new RuntimeException("Album does not exist."));
        return new AlbumDto(album.getArtistId(), album.getName(), album.getReleaseDate(), album.getCoverUrl());
    }

    @Override
    public void updateAlbumById(UpdateAlbumDto dto, UUID id) {
        Album album = albumRepository.findById(id).orElseThrow(() -> new RuntimeException("Album does not exist."));
        if (dto.name() != null) album.setName(dto.name());
        if (dto.releaseDate() != null) album.setReleaseDate(dto.releaseDate());
        if (dto.coverUrl() != null) album.setCoverUrl(dto.coverUrl());
        albumRepository.save(album);
    }

    @Override
    @SneakyThrows
    public void updateAlbumCover(UUID id, MultipartFile coverFile) {
        Album album = albumRepository.findById(id).orElseThrow(() -> new RuntimeException("Album does not exist."));

        Path tempCover = null;
        try {
            minioUtils.createBucket(artworkBucket);
            tempCover = Files.createTempFile("cover-", coverFile.getOriginalFilename());
            coverFile.transferTo(tempCover.toFile());

            String newObjectName = UUID.randomUUID() + "-" + coverFile.getOriginalFilename();
            minioUtils.uploadFile(artworkBucket, newObjectName, tempCover.toString());
            String newCoverUrl = minioUtils.getPresignedObjectUrl(artworkBucket, newObjectName);

            String oldCoverUrl = album.getCoverUrl();
            if (oldCoverUrl != null) {
                String oldObject = minioUtils.extractObjectNameFromUrl(oldCoverUrl, artworkBucket);
                if (oldObject != null && !oldObject.isBlank()) {
                    try {
                        minioUtils.deleteObject(artworkBucket, oldObject);
                    } catch (Exception ignored) {}
                }
            }

            album.setCoverUrl(newCoverUrl);
            albumRepository.save(album);
        } finally {
            if (tempCover != null) {
                Files.deleteIfExists(tempCover);
            }
        }
    }

    @Override
    @SneakyThrows
    public void addTracks(UUID albumId, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("No files provided.");
        }
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Album does not exist."));

        Set<UUID> trackIds = album.getTrackIds();
        if (trackIds == null) trackIds = new HashSet<>();

        minioUtils.createBucket(trackBucket);

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) continue;
            Path tempTrack = null;
            try {
                String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "audio";
                tempTrack = Files.createTempFile("track-", originalName);
                file.transferTo(tempTrack.toFile());

                String objectName = UUID.randomUUID() + "-" + originalName;
                minioUtils.uploadFile(trackBucket, objectName, tempTrack.toString());
                String trackUrl = minioUtils.getPresignedObjectUrl(trackBucket, objectName);

                Track track = new Track();
                track.setArtistId(album.getArtistId());
                track.setName(stripExtension(originalName));
                track.setArtistName(null);
                track.setArtworkUrl(null);
                track.setTrackUrl(trackUrl);
                track = trackRepository.save(track);

                trackIds.add(track.getId());
            } finally {
                if (tempTrack != null) {
                    try { Files.deleteIfExists(tempTrack); } catch (Exception e) { log.warn("Failed to delete temp track file", e); }
                }
            }
        }

        album.setTrackIds(trackIds);
        albumRepository.save(album);
    }

    @Override
    public void addTrackIds(UUID albumId, List<UUID> newTrackIds) {
        if (newTrackIds == null || newTrackIds.isEmpty()) {
            throw new IllegalArgumentException("No track IDs provided.");
        }
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Album does not exist."));

        List<Track> tracks = trackRepository.findAllById(newTrackIds);
        if (tracks.size() != newTrackIds.size()) {
            throw new IllegalArgumentException("One or more track IDs do not exist.");
        }

        Set<UUID> trackIds = album.getTrackIds();
        if (trackIds == null) trackIds = new HashSet<>();
        for (Track t : tracks) {
            trackIds.add(t.getId());
        }
        album.setTrackIds(trackIds);
        albumRepository.save(album);
    }

    private String stripExtension(String filename) {
        if (filename == null) return null;
        int idx = filename.lastIndexOf('.');
        return (idx > 0) ? filename.substring(0, idx) : filename;
    }
}
