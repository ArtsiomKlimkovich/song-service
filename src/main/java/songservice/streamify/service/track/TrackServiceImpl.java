package songservice.streamify.service.track;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import songservice.streamify.dto.TrackDto;
import songservice.streamify.entity.Track;
import songservice.streamify.repository.TrackRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrackServiceImpl implements TrackService{
    private final TrackRepository trackRepository;

    @Override
    public void addTrack(TrackDto dto) {
        Track track = new Track();
        track.setArtistId(dto.artistId());
        track.setName(dto.name());
        track.setArtistName(dto.artistName());
        trackRepository.save(track);
    }

    @Override
    public TrackDto getTrackById(UUID id) {
        Track track = trackRepository.findById(id).orElseThrow(() -> new RuntimeException("Track does not exist."));
        return new TrackDto(track.getArtistId(), track.getName(), track.getArtistName());
    }

    @Override
    public TrackDto updateTrackById(TrackDto dto, UUID id) {
        Track track = trackRepository.findById(id).orElseThrow(() -> new RuntimeException("Track does not exist."));
        track.setArtistId(dto.artistId());
        track.setName(dto.name());
        track.setArtistName(dto.artistName());
        trackRepository.save(track);
        return dto;
    }

    @Override
    public void deleteTrackById(UUID id) {
        Track track = trackRepository.findById(id).orElseThrow(() -> new RuntimeException("Track does not exist."));
        trackRepository.delete(track);
    }
}