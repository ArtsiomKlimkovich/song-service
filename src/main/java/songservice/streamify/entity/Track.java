package songservice.streamify.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private String trackUrl;

    private String artworkUrl;
    // more
}
