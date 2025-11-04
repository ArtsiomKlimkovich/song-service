package songservice.streamify.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.repository.Modifying;

import java.util.UUID;

// LATER SWITCH TO CASSANDRA
@jakarta.persistence.Entity
@Getter
@Setter
@Table(name = "tracks")
@AllArgsConstructor
@NoArgsConstructor
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID artistId;

    private String name;

    private String artistName;

    @Column(name = "artwork_url", length = 1000)
    private String artworkUrl;

    @Column(name = "track_url", length = 1000)
    private String trackUrl;

    // more
}