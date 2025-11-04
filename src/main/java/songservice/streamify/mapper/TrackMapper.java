package songservice.streamify.mapper;

import org.mapstruct.*;
import songservice.streamify.dto.track.CreateTrackDto;
import songservice.streamify.dto.track.TrackDto;
import songservice.streamify.dto.track.UpdateTrackDto;
import songservice.streamify.entity.Track;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TrackMapper {

    TrackDto toDto(Track track);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTrackFromDto(UpdateTrackDto dto, @MappingTarget Track track);
}
