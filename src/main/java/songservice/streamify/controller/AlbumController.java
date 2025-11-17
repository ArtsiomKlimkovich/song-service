package songservice.streamify.controller;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import songservice.streamify.dto.album.AlbumDto;
import songservice.streamify.dto.album.CreateAlbumDto;
import songservice.streamify.dto.album.UpdateAlbumDto;
import songservice.streamify.service.album.AlbumService;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/albums")
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService albumService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<String> createAlbum(
            @RequestParam("artistId") UUID artistId,
            @RequestParam("name") String name,
            @RequestParam("releaseDate") LocalDate releaseDate,
            @RequestParam("file") MultipartFile cover) throws FileUploadException {
        CreateAlbumDto dto = new CreateAlbumDto(artistId, name, releaseDate, cover);
        albumService.createAlbum(dto);
        return ResponseEntity.ok("Album created successfully.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumDto> getAlbumById(@PathVariable UUID id){
        AlbumDto dto = albumService.getAlbumById(id);
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateAlbumById(@RequestBody UpdateAlbumDto dto, @PathVariable UUID id){
        albumService.updateAlbumById(id, dto);
        return ResponseEntity.ok("Album updated successfully.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAlbumById(@PathVariable UUID id){
        albumService.deleteAlbumById(id);
        return ResponseEntity.ok("Album deleted successfully");
    }

    @PatchMapping(value = "/{id}/cover", consumes = "multipart/form-data")
    public ResponseEntity<String> updateCover(@PathVariable UUID id, @RequestParam MultipartFile cover) throws FileUploadException {
        albumService.updateAlbumCover(id, cover);
        return ResponseEntity.ok("Album cover successfully updated.");
    }

    @PostMapping("/{artistId}")
    public ResponseEntity<String> addTrackToAlbum(@PathVariable UUID artistId, UUID id){
        albumService.addTrackToAlbum(artistId, id);
        return ResponseEntity.ok("Track successfully added to album.");
    }
}
