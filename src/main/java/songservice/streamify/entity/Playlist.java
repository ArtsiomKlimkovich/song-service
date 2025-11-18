package songservice.streamify.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "playlists")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private UUID userId;
    private String name;

    @CreationTimestamp
    private LocalDateTime creationDate;

    @Column(name = "cover_url", length = 1000)
    private String coverUrl;

    @ElementCollection
    @CollectionTable(name = "playlist_tracks", joinColumns = @JoinColumn(name = "playlist_id"))
    @Column(name = "track_id")
    private Map<UUID, LocalDateTime> trackIds = new LinkedHashMap<>();

    public Map<UUID, LocalDateTime> getTrackIds(){
        if (trackIds == null){
            trackIds = new LinkedHashMap<>();
        }
        return trackIds;
    }
}
