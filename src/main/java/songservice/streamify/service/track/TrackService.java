package songservice.streamify.service.track;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.web.multipart.MultipartFile;
import songservice.streamify.dto.track.CreateTrackDto;
import songservice.streamify.dto.track.TrackDto;
import songservice.streamify.dto.track.UpdateTrackDto;
import songservice.streamify.entity.Track;

import java.util.UUID;

public interface TrackService {
    void addTrack(CreateTrackDto dto);
    TrackDto getTrackById(UUID id);
    void updateTrackById(UUID id, UpdateTrackDto dto);
    void updateTrackArtwork(UUID id, MultipartFile artworkFile) throws FileUploadException;
    void deleteTrackById(UUID id);
}