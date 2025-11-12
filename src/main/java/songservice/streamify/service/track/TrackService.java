package songservice.streamify.service.track;

import org.springframework.web.multipart.MultipartFile;
import songservice.streamify.dto.track.CreateTrackDto;
import songservice.streamify.dto.track.TrackDto;
import songservice.streamify.dto.track.UpdateTrackDto;

import java.util.UUID;

public interface TrackService {
    void addTrack(CreateTrackDto dto);
    TrackDto getTrackById(UUID id);
    void updateTrackById(UpdateTrackDto dto, UUID id);
    void updateTrackArtwork(UUID id, MultipartFile artworkFile);
    void deleteTrackById(UUID id);
}