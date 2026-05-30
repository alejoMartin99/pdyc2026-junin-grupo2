package pdyc.greater_events.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pdyc.greater_events.dtos.EventoDto;
import pdyc.greater_events.entities.Evento;
import pdyc.greater_events.enums.EventState;
import pdyc.greater_events.services.EventoService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/events")
public class PublicEventController {

    private final EventoService eventoService;

    public PublicEventController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @GetMapping
    public ResponseEntity<List<EventoDto>> getEvents() {
        // list events that are confirmed or rescheduled and in the future or today
        List<EventoDto> dtos = eventoService.devolverEventos(null).stream()
                .filter(d -> {
                    LocalDate fecha = d.getFechaRealizacionDto();
                    return fecha != null && (fecha.isAfter(LocalDate.now()) || fecha.isEqual(LocalDate.now()))
                            && (d.getEstadoDto() == EventState.Confirmed || d.getEstadoDto() == EventState.Rescheduled);
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventoDto> getEvent(@PathVariable Long eventId) {
        EventoDto dto = eventoService.devolverEventoDtoPorId(eventId);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }
}


/*PublicEventController
Endpoints públicos de eventos (/events/*).

GET /events: lista eventos confirmados/reprogramados que aún no pasaron (sin autenticación)
GET /events/{id}: detalles de un evento
 */