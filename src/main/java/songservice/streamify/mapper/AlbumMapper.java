package songservice.streamify.mapper;

import org.mapstruct.*;
import songservice.streamify.dto.album.AlbumDto;
import songservice.streamify.dto.album.UpdateAlbumDto;
import songservice.streamify.entity.Album;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlbumMapper {
    AlbumDto toDto(Album album);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAlbumFromDto(UpdateAlbumDto dto, @MappingTarget Album album);
}
