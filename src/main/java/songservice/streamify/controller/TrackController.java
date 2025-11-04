package songservice.streamify.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import songservice.streamify.dto.track.CreateTrackDto;
import songservice.streamify.dto.track.TrackDto;
import songservice.streamify.dto.track.UpdateTrackDto;
import songservice.streamify.service.track.TrackService;

import java.util.UUID;

@RequestMapping("/api/v1/tracks")
@RestController
@RequiredArgsConstructor
public class TrackController {
    private final TrackService trackService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<String> addTrack(
            @RequestParam("artistId") UUID artistId,
            @RequestParam("name") String name,
            @RequestParam("artistName") String artistName,
            @RequestParam("artwork") MultipartFile artwork,
            @RequestParam("file") MultipartFile file){
        CreateTrackDto dto = new CreateTrackDto(artistId, name, artistName, artwork, file);
        trackService.addTrack(dto);
        return ResponseEntity.ok("Track added successfully.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrackDto> getTrackById(@PathVariable UUID id){
        TrackDto dto = trackService.getTrackById(id);
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TrackDto> updateTrackById(@RequestBody UpdateTrackDto dto, @PathVariable UUID id){
        trackService.updateTrackById(dto, id);
        TrackDto updated = trackService.getTrackById(id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTrackById(@PathVariable UUID id){
        trackService.deleteTrackById(id);
        return ResponseEntity.ok("track was successfully deleted.");
    }
}
