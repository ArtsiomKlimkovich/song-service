package songservice.streamify.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "albums")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID artistId;

    private String name;

    private Date releaseDate;

    @Column(name = "cover_url", length = 1000)
    private String coverUrl;

    @ElementCollection
    @CollectionTable(name = "album_tracks", joinColumns = @JoinColumn(name = "album_id"))
    @Column(name = "track_id")
    private Set<UUID> trackIds; // denormalized for easier Cassandra implementation in the future
}
