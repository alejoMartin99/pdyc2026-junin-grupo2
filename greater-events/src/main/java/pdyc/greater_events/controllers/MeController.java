package pdyc.greater_events.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pdyc.greater_events.dtos.ArtistaDto;
import pdyc.greater_events.dtos.EventoDto;
import pdyc.greater_events.entities.Artista;
import pdyc.greater_events.entities.Evento;
import pdyc.greater_events.services.UserService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/me")
public class MeController {

    private final UserService userService;

    public MeController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/following")
    public ResponseEntity<Void> follow(@RequestParam Long artistId, @RequestHeader("Authorization") String authHeader) {
        String username = extractUsernameFromAuthHeader(authHeader);
        boolean ok = userService.followArtist(username, artistId);
        return ok ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/following/{artistId}")
    public ResponseEntity<Void> unfollow(@PathVariable Long artistId, @RequestHeader("Authorization") String authHeader) {
        String username = extractUsernameFromAuthHeader(authHeader);
        boolean ok = userService.unfollowArtist(username, artistId);
        return ok ? ResponseEntity.noContent().build() : ResponseEntity.badRequest().build();
    }

    @GetMapping("/following")
    public ResponseEntity<List<ArtistaDto>> getFollowing(@RequestHeader("Authorization") String authHeader) {
        String username = extractUsernameFromAuthHeader(authHeader);
        List<Artista> list = userService.getFollowing(username);
        List<ArtistaDto> dtos = list.stream().map(a -> new ArtistaDto(a.getNombre(), a.getGenero())).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/favorite-events")
    public ResponseEntity<Void> favEvent(@RequestParam Long eventId, @RequestHeader("Authorization") String authHeader) {
        String username = extractUsernameFromAuthHeader(authHeader);
        boolean ok = userService.favoriteEvent(username, eventId);
        return ok ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/favorite-events/{eventId}")
    public ResponseEntity<Void> unfavEvent(@PathVariable Long eventId, @RequestHeader("Authorization") String authHeader) {
        String username = extractUsernameFromAuthHeader(authHeader);
        boolean ok = userService.unfavoriteEvent(username, eventId);
        return ok ? ResponseEntity.noContent().build() : ResponseEntity.badRequest().build();
    }

    @GetMapping("/favorite-events")
    public ResponseEntity<List<EventoDto>> getFavorites(@RequestHeader("Authorization") String authHeader) {
        String username = extractUsernameFromAuthHeader(authHeader);
        List<Evento> events = userService.getFavoriteEvents(username);
        List<EventoDto> dtos = events.stream().map(e -> new EventoDto(e.getNombre(), e.getDescripcion(), e.getFechaRealizacion(), e.getEventState(), e.getArtistas().size())).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/following/events")
    public ResponseEntity<List<EventoDto>> getFollowingEvents(@RequestHeader("Authorization") String authHeader) {
        String username = extractUsernameFromAuthHeader(authHeader);
        List<Evento> events = userService.getFollowingEvents(username);
        List<EventoDto> dtos = events.stream().map(e -> new EventoDto(e.getNombre(), e.getDescripcion(), e.getFechaRealizacion(), e.getEventState(), e.getArtistas().size())).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    private String extractUsernameFromAuthHeader(String authHeader) {
        if (authHeader == null) return null;
        if (!authHeader.startsWith("Bearer ")) return null;
        String token = authHeader.substring(7);
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return null;
            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
            Map map = om.readValue(payload, java.util.Map.class);
            Object pref = map.get("preferred_username");
            if (pref == null) pref = map.get("sub");
            return pref == null ? null : String.valueOf(pref);
        } catch (Exception e) {
            return null;
        }
    }
}


/*MeController
Endpoints de usuario autenticado (/me/*).

POST /me/following?artistId=X: seguir artista
DELETE /me/following/{artistId}: dejar de seguir
GET /me/following: listar artistas que sigo
POST /me/favorite-events?eventId=X: marcar evento favorito
DELETE /me/favorite-events/{eventId}: desmarcar favorito
GET /me/favorite-events: listar favoritos
GET /me/following/events: listar eventos próximos de artistas que sigo
Extrae username del JWT para identificar al usuario */