package songservice.streamify.mapper;

import org.mapstruct.*;
import songservice.streamify.dto.album.UpdateAlbumDto;
import songservice.streamify.dto.playlist.PlaylistDto;
import songservice.streamify.dto.playlist.UpdatePlaylistDto;
import songservice.streamify.entity.Album;
import songservice.streamify.entity.Playlist;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PlaylistMapper {
    PlaylistDto toDto(Playlist playlist);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePlaylistFromDto(UpdatePlaylistDto dto, @MappingTarget Playlist playlist);
}
