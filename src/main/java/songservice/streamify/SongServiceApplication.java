package songservice.streamify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SongServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SongServiceApplication.class, args);
    }

}

/*
TABLES FOR CASSANDRA LATER
Track
Album
Playlist
Tracks by Album
Tracks by Playlist
Tracks by User
Tracks by Artist
*/