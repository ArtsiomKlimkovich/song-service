package songservice.streamify.service.track;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import songservice.streamify.dto.track.CreateTrackDto;
import songservice.streamify.dto.track.TrackDto;
import songservice.streamify.dto.track.UpdateTrackDto;
import songservice.streamify.entity.Track;
import songservice.streamify.repository.TrackRepository;
import songservice.streamify.utils.MinioService;
import songservice.streamify.mapper.TrackMapper;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackServiceImpl implements TrackService{
    private final TrackRepository trackRepository;
    private final MinioService minioService;
    private final TrackMapper trackMapper;

    @Value("${minio.buckets.track}")
    private String trackBucket;

    @Value("${minio.buckets.artwork}")
    private String artworkBucket;

    @Override
    public void addTrack(CreateTrackDto dto) {
        CompletableFuture<String> trackUrlFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return minioService.uploadFile(dto.file(), trackBucket, "tracks/" + dto.artistId() + "/");
            } catch (FileUploadException e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture<String> artworkUrlFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return minioService.uploadFile(dto.artwork(), artworkBucket, "artwork/" + dto.artistId() + "/");
            } catch (FileUploadException e) {
                throw new RuntimeException(e);
            }
        });

        String trackUrl = trackUrlFuture.join();
        String artworkUrl = artworkUrlFuture.join();

        Track track = Track.builder()
                .artistId(dto.artistId())
                .name(dto.name())
                .artistName(dto.artistName())
                .trackUrl(trackUrl)
                .artworkUrl(artworkUrl)
                .build();

        trackRepository.save(track);
    }

    @Override
    public TrackDto getTrackById(UUID id) {
        return trackRepository.findById(id)
                .map(trackMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Track not found: " + id));
    }

    @Override
    public void updateTrackById(UUID id, UpdateTrackDto dto) {
        Track track = trackRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Track not found: " + id));
        trackMapper.updateTrackFromDto(dto, track);
        trackRepository.save(track);
    }

    @Override
    public void updateTrackArtwork(UUID trackId, MultipartFile newArtwork) throws FileUploadException {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new EntityNotFoundException("Track not found: " + trackId));

        String oldUrl = track.getArtworkUrl();
        String newUrl = minioService.uploadFile(newArtwork, artworkBucket,
                "artwork/" + track.getArtistId() + "/");

        track.setArtworkUrl(newUrl);
        trackRepository.save(track);

        if (oldUrl != null) {
            String oldObjectName = minioService.extractObjectNameFromUrl(oldUrl, artworkBucket);
            if (oldObjectName != null) {
                minioService.deleteObject(artworkBucket, oldObjectName);
            }
        }
    }

    @Override
    public void deleteTrackById(UUID id) {
        Track track = trackRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Track not found: " + id));

        try {
            if (track.getTrackUrl() != null) {
                String obj = minioService.extractObjectNameFromUrl(track.getTrackUrl(), trackBucket);
                if (obj != null) minioService.deleteObject(trackBucket, obj);
            }
            if (track.getArtworkUrl() != null) {
                String obj = minioService.extractObjectNameFromUrl(track.getArtworkUrl(), artworkBucket);
                if (obj != null) minioService.deleteObject(artworkBucket, obj);
            }
        } catch (Exception e) {
            log.warn("Failed to cleanup MinIO objects for track {}", id, e);
        }

        trackRepository.deleteById(id);
    }
}