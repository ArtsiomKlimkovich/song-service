package songservice.streamify.service.track;

import songservice.streamify.dto.track.CreateTrackDto;
import songservice.streamify.dto.track.TrackDto;
import songservice.streamify.dto.track.UpdateTrackDto;

import java.util.UUID;

public interface TrackService {
    void addTrack(CreateTrackDto dto);
    TrackDto getTrackById(UUID id);
    void updateTrackById(UpdateTrackDto dto, UUID id);
    void deleteTrackById(UUID id);
}