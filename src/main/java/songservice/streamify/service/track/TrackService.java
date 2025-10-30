package songservice.streamify.service.track;

import songservice.streamify.dto.TrackDto;

import java.util.UUID;

public interface TrackService {
    public void addTrack(TrackDto dto);
    public TrackDto getTrackById(UUID id);
    public TrackDto updateTrackById(TrackDto dto, UUID id);
    public void deleteTrackById(UUID id);
}