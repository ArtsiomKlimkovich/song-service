package songservice.streamify.service.track;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import songservice.streamify.dto.track.CreateTrackDto;
import songservice.streamify.dto.track.TrackDto;
import songservice.streamify.dto.track.UpdateTrackDto;
import songservice.streamify.entity.Track;
import songservice.streamify.repository.TrackRepository;
import songservice.streamify.utils.MinioUtils;
import songservice.streamify.mapper.TrackMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackServiceImpl implements TrackService{
    private final TrackRepository trackRepository;
    private final MinioUtils minioUtils;
    private final TrackMapper trackMapper;

    @Value("${minio.buckets.track}")
    private String trackBucket;

    @Value("${minio.buckets.artwork}")
    private String artworkBucket;

    @Override
    @SneakyThrows
    public void addTrack(CreateTrackDto dto) {
        Path tempTrackFile = null;
        Path tempArtworkFile = null;
        try {
            tempTrackFile = Files.createTempFile("track-", dto.file().getOriginalFilename());
            dto.file().transferTo(tempTrackFile.toFile());

            tempArtworkFile = Files.createTempFile("artwork-", dto.artwork().getOriginalFilename());
            dto.artwork().transferTo(tempArtworkFile.toFile());

            minioUtils.createBucket(trackBucket);
            minioUtils.createBucket(artworkBucket);

            String trackObjectName = UUID.randomUUID() + "-" + dto.file().getOriginalFilename();
            String artworkObjectName = UUID.randomUUID() + "-" + dto.artwork().getOriginalFilename();

            minioUtils.uploadFile(trackBucket, trackObjectName, tempTrackFile.toString());
            minioUtils.uploadFile(artworkBucket, artworkObjectName, tempArtworkFile.toString());

            String trackUrl = minioUtils.getPresignedObjectUrl(trackBucket, trackObjectName);
            String artworkUrl = minioUtils.getPresignedObjectUrl(artworkBucket, artworkObjectName);

            Track track = new Track();
            track.setArtistId(dto.artistId());
            track.setName(dto.name());
            track.setArtistName(dto.artistName());
            track.setTrackUrl(trackUrl);
            track.setArtworkUrl(artworkUrl);
            trackRepository.save(track);
        } finally {
            if (tempTrackFile != null) {
                Files.deleteIfExists(tempTrackFile);
            }
            if (tempArtworkFile != null) {
                Files.deleteIfExists(tempArtworkFile);
            }
        }
    }

    @Override
    public TrackDto getTrackById(UUID id) {
        Track track = trackRepository.findById(id).orElseThrow(() -> new RuntimeException("Track does not exist."));
        return trackMapper.toDto(track);
    }

    @Override
    public void updateTrackById(UpdateTrackDto dto, UUID id) {
        Track track = trackRepository.findById(id).orElseThrow(() -> new RuntimeException("Track does not exist."));
        trackMapper.updateTrackFromDto(dto, track);
        trackRepository.save(track);
    }

    @Override
    @SneakyThrows
    public void updateTrackArtwork(UUID id, MultipartFile artworkFile) {
        Track track = trackRepository.findById(id).orElseThrow(() -> new RuntimeException("Track does not exist."));

        Path tempArtworkFile = null;
        try {
            minioUtils.createBucket(artworkBucket);
            tempArtworkFile = Files.createTempFile("artwork-", artworkFile.getOriginalFilename());
            artworkFile.transferTo(tempArtworkFile.toFile());

            String newArtworkObjectName = UUID.randomUUID() + "-" + artworkFile.getOriginalFilename();
            minioUtils.uploadFile(artworkBucket, newArtworkObjectName, tempArtworkFile.toString());
            String newArtworkUrl = minioUtils.getPresignedObjectUrl(artworkBucket, newArtworkObjectName);

            String oldArtworkUrl = track.getArtworkUrl();
            if (oldArtworkUrl != null) {
                String oldObjectName = minioUtils.extractObjectNameFromUrl(oldArtworkUrl, artworkBucket);
                if (oldObjectName != null && !oldObjectName.isBlank()) {
                    try {
                        minioUtils.deleteObject(artworkBucket, oldObjectName);
                    } catch (Exception e) {
                        log.warn("Failed to delete old artwork object: {}", oldObjectName, e);
                    }
                }
            }

            track.setArtworkUrl(newArtworkUrl);
            trackRepository.save(track);
        } finally {
            if (tempArtworkFile != null) {
                Files.deleteIfExists(tempArtworkFile);
            }
        }
    }

    @Override
    public void deleteTrackById(UUID id) {
        Track track = trackRepository.findById(id).orElseThrow(() -> new RuntimeException("Track does not exist."));
        trackRepository.delete(track);
    }
}