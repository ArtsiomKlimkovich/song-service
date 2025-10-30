package songservice.streamify.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import songservice.streamify.dto.TrackDto;
import songservice.streamify.service.track.TrackService;

import java.util.UUID;

@RequestMapping("/api/v1/tracks")
@RestController
@RequiredArgsConstructor
public class TrackController {
    private final TrackService trackService;

    @PostMapping
    public ResponseEntity<String> addTrack(@RequestBody TrackDto dto){
        trackService.addTrack(dto);
        return ResponseEntity.ok("Track added successfully.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrackDto> getTrackById(@PathVariable UUID id){
        TrackDto dto = trackService.getTrackById(id);
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TrackDto> updateTrackById(@RequestBody TrackDto dto, @PathVariable UUID id){
        trackService.updateTrackById(dto, id);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTrackById(@PathVariable UUID id){
        trackService.deleteTrackById(id);
        return ResponseEntity.ok("track was successfully deleted.");
    }
}
