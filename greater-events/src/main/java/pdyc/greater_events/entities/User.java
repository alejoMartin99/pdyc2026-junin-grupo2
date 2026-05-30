package pdyc.greater_events.entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true)
    private String email;

    @ManyToMany
    @JoinTable(name = "user_following_artists",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    private List<Artista> followingArtists = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "user_favorite_events",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private List<Evento> favoriteEvents = new ArrayList<>();

    public User() {}

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<Artista> getFollowingArtists() { return followingArtists; }
    public void setFollowingArtists(List<Artista> followingArtists) { this.followingArtists = followingArtists; }
    public List<Evento> getFavoriteEvents() { return favoriteEvents; }
    public void setFavoriteEvents(List<Evento> favoriteEvents) { this.favoriteEvents = favoriteEvents; }
}
