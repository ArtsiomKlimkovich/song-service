package songservice.streamify.controller;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import songservice.streamify.dto.playlist.CreatePlaylistDto;
import songservice.streamify.dto.playlist.PlaylistDto;
import songservice.streamify.dto.playlist.UpdatePlaylistDto;
import songservice.streamify.service.playlist.PlaylistService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/playlists")
@RequiredArgsConstructor
public class PlaylistController {
    private final PlaylistService playlistService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<String> createPlaylist(
            @RequestParam("userId") UUID userId,
            @RequestParam("name") String name,
            @RequestParam("cover") MultipartFile cover) throws FileUploadException {
        CreatePlaylistDto dto = new CreatePlaylistDto(userId, name, cover);
        playlistService.createPlaylist(dto);
        return ResponseEntity.ok("Album created successfully.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistDto> getPlaylistById(@PathVariable UUID id){
        PlaylistDto dto = playlistService.getPlaylistById(id);
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updatePlaylistById(@PathVariable UUID id, @RequestBody UpdatePlaylistDto dto){
        playlistService.updatePlaylistById(id, dto);
        return ResponseEntity.ok("Playlist updated successfully.");
    }

    @PatchMapping(value = "/{id}/cover", consumes = "multipart/form-data")
    public ResponseEntity<String> updateCover(@PathVariable UUID id, @RequestParam MultipartFile cover) throws FileUploadException {
        playlistService.updatePlaylistCover(id, cover);
        return ResponseEntity.ok("Playlist cover successfully updated.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAlbumById(@PathVariable UUID id){
        playlistService.deletePlaylistById(id);
        return ResponseEntity.ok("Playlist successfully deleted.");
    }

    @PostMapping("/{playlistId}")
    public ResponseEntity<String> addTrackToPlaylist(@PathVariable UUID playlistId, @RequestParam UUID trackId){
        playlistService.addTrackToPlaylist(playlistId, trackId);
        return ResponseEntity.ok("Track successfully added to playlist.");
    }
}
