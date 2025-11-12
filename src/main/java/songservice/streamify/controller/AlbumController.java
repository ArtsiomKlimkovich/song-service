package songservice.streamify.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import songservice.streamify.dto.album.AlbumDto;
import songservice.streamify.dto.album.CreateAlbumDto;
import songservice.streamify.dto.album.UpdateAlbumDto;
import songservice.streamify.service.album.AlbumService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/albums")
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService albumService;

    @PostMapping
    public ResponseEntity<String> createAlbum(@RequestBody CreateAlbumDto dto){
        albumService.createAlbum(dto);
        return ResponseEntity.ok("Album created successfully.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumDto> getAlbumById(@PathVariable UUID id){
        return ResponseEntity.ok(albumService.getAlbumById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AlbumDto> updateAlbum(@RequestBody UpdateAlbumDto dto, @PathVariable UUID id){
        albumService.updateAlbumById(dto, id);
        return ResponseEntity.ok(albumService.getAlbumById(id));
    }

    @PatchMapping(value = "/{id}/cover", consumes = "multipart/form-data")
    public ResponseEntity<AlbumDto> updateAlbumCover(@PathVariable UUID id, @RequestParam("cover") MultipartFile cover){
        albumService.updateAlbumCover(id, cover);
        return ResponseEntity.ok(albumService.getAlbumById(id));
    }

    @PostMapping(value = "/{id}/tracks", consumes = "multipart/form-data")
    public ResponseEntity<String> addTracksToAlbum(@PathVariable UUID id, @RequestParam("files") List<MultipartFile> files) {
        albumService.addTracks(id, files);
        return ResponseEntity.ok("Tracks added to album successfully.");
    }

    @PostMapping(value = "/{id}/tracks/ids", consumes = "application/json")
    public ResponseEntity<String> addExistingTracksToAlbum(@PathVariable UUID id, @RequestBody List<UUID> trackIds) {
        albumService.addTrackIds(id, trackIds);
        return ResponseEntity.ok("Track IDs added to album successfully.");
    }
}
