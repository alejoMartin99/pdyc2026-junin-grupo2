package pdyc.greater_events.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pdyc.greater_events.dtos.ArtistaDto;
import pdyc.greater_events.dtos.EventoDto;
import pdyc.greater_events.entities.Artista;
import pdyc.greater_events.entities.Evento;
import pdyc.greater_events.services.ArtistaService;
import pdyc.greater_events.services.EventoService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/artists")
public class PublicArtistController {

    private final ArtistaService artistaService;
    private final EventoService eventoService;

    public PublicArtistController(ArtistaService artistaService, EventoService eventoService) {
        this.artistaService = artistaService;
        this.eventoService = eventoService;
    }

    @GetMapping
    public ResponseEntity<List<ArtistaDto>> getArtists() {
        return ResponseEntity.ok(artistaService.getArtists(null));
    }

    @GetMapping("/{artistId}/events")
    public ResponseEntity<List<EventoDto>> getArtistEvents(@PathVariable Long artistId){
        Artista a = artistaService.findById(artistId);
        if (a == null) return ResponseEntity.notFound().build();
        List<Evento> eventos = a.getEventos().stream()
                .filter(e -> e.getEventState() == null || (e.getEventState().name().equals("Confirmed") || e.getEventState().name().equals("Rescheduled")))
                .collect(Collectors.toList());
        List<EventoDto> dtos = eventoService.transformarDto(eventos);
        return ResponseEntity.ok(dtos);
    }
}


/*PublicArtistController
Endpoints públicos de artistas (/artists/*).

GET /artists: lista todos los artistas (sin autenticación)
GET /artists/{id}/events: lista eventos confirmados/reprogramados del artista
 */