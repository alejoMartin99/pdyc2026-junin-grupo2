package pdyc.greater_events.services;

import org.springframework.stereotype.Service;
import pdyc.greater_events.entities.Artista;
import pdyc.greater_events.entities.Evento;
import pdyc.greater_events.entities.User;
import pdyc.greater_events.repositories.UserRepository;

import java.time.LocalDate;
import pdyc.greater_events.enums.EventState;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ArtistaService artistaService;
    private final EventoService eventoService;

    public UserService(UserRepository userRepository, ArtistaService artistaService, EventoService eventoService) {
        this.userRepository = userRepository;
        this.artistaService = artistaService;
        this.eventoService = eventoService;
    }
    //el usuario se registra en Klaycloak y lo cargo en la BD.
    public User register(String username, String email) {
        Optional<User> ex = userRepository.findByUsername(username);
        if (ex.isPresent()) return null;
        User u = new User(username, email);
        return userRepository.save(u);
    }
    //seguir a artista si el username es correcto y el artista existe.
    public boolean followArtist(String username, Long artistId) {
        Optional<User> o = userRepository.findByUsername(username);
        if (o.isEmpty()) return false;
        User u = o.get();
        Artista a = artistaService.findById(artistId);
        if (a == null) return false;
        if (!u.getFollowingArtists().contains(a)) u.getFollowingArtists().add(a);
        userRepository.save(u);
        return true;
    }
    //dejo de seguir a un artista.
    public boolean unfollowArtist(String username, Long artistId) {
        Optional<User> o = userRepository.findByUsername(username);
        if (o.isEmpty()) return false;
        User u = o.get();
        boolean removed = u.getFollowingArtists().removeIf(a -> a.getIdArtista().equals(artistId));
        if (removed) userRepository.save(u);
        return removed;
    }
    //obtener lista de artistas que sigue un usuario.
    public List<Artista> getFollowing(String username) {
        return userRepository.findByUsername(username).map(User::getFollowingArtists).orElse(List.of());
    }
    //buscar un evento por id y marcarlo como favorito para el usuario.
    public boolean favoriteEvent(String username, Long eventId) {
        Optional<User> o = userRepository.findByUsername(username);
        if (o.isEmpty()) return false;
        User u = o.get();
        Evento e = eventoService.findById(eventId);
        if (e == null) return false;
        if (!u.getFavoriteEvents().contains(e)) u.getFavoriteEvents().add(e);
        userRepository.save(u);
        return true;
    }
    //remover un evento de favoritos del usuario.
    public boolean unfavoriteEvent(String username, Long eventId) {
        Optional<User> o = userRepository.findByUsername(username);
        if (o.isEmpty()) return false;
        User u = o.get();
        boolean removed = u.getFavoriteEvents().removeIf(ev -> ev.getIdEvento().equals(eventId));
        if (removed) userRepository.save(u);
        return removed;
    }
    //lista eventos favoritos de un usuario que aún no pasaron y están confirmados o reprogramados.
    public List<Evento> getFavoriteEvents(String username) {
        LocalDate today = LocalDate.now();
        return userRepository.findByUsername(username)
                .map(User::getFavoriteEvents)
                .orElse(List.of())
                .stream()
                .filter(e -> e.getFechaRealizacion() != null && (e.getEventState() == EventState.Confirmed || e.getEventState() == EventState.Rescheduled) && (e.getFechaRealizacion().isAfter(today) || e.getFechaRealizacion().isEqual(today)))
                .collect(Collectors.toList());
    }
    //lista eventos próximos de artistas que sigue el usuario.
    public List<Evento> getFollowingEvents(String username) {
        LocalDate today = LocalDate.now();
        return userRepository.findByUsername(username).map(u ->
                u.getFollowingArtists().stream()
                        .flatMap(a -> a.getEventos().stream())
                        .filter(e -> e.getFechaRealizacion() != null && (e.getFechaRealizacion().isAfter(today) || e.getFechaRealizacion().isEqual(today)))
                        .filter(e -> e.getEventState() == null || (!e.getEventState().name().equals("Tentative")))
                        .distinct()
                        .sorted(Comparator.comparing(Evento::getFechaRealizacion))
                        .collect(Collectors.toList())
        ).orElse(List.of());
    }
}

/* 
UserService
Gestiona registros de usuario en BD local.

register(): guarda nuevo usuario en BD
followArtist() / unfollowArtist(): seguir/dejar de seguir artista
favoriteEvent() / unfavoriteEvent(): marcar evento como favorito
getFollowing(), getFavoriteEvents(), getFollowingEvents(): lista sus datos
*/