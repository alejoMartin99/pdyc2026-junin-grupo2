package pdyc.greater_events.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pdyc.greater_events.dtos.EventoDto;
import pdyc.greater_events.entities.Artista;
import pdyc.greater_events.entities.Evento;
import pdyc.greater_events.enums.EventState;
import pdyc.greater_events.services.ArtistaService;
import pdyc.greater_events.services.EventoService;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/admin/events")
public class EventoController {
  @Autowired
  private EventoService eventoService;
  @Autowired
  private ArtistaService artistaService;

  /* 
  Get Events
  Retorna un listado de eventos. Cada evento del listado tendrá el nombre, fecha, estado y la
  cantidad de artistas asignados. Se puede especificar el estado para filtrar resultados.
  GET /admin/events?state=:state */
  @GetMapping("/getEvents")
  public ResponseEntity<List<EventoDto>> getEvents(@RequestParam(required = false) EventState state) {
    List<EventoDto> eventosDtos = eventoService.devolverEventos(state);
    return ResponseEntity.ok(eventosDtos);
  }
  
  /*Get Event
  Retorna los detalles de un evento específico retornando también el listado de los artistas del
  la mismo.
  GET /admin/events/:id */
  @GetMapping("/{id}")
  //@PathVariable ya que el dato forma parte del endpoint
  public ResponseEntity<EventoDto> getEventoPorId(@PathVariable Long id){
    EventoDto eventoDtoId = eventoService.devolverEventoDtoPorId(id);
    return (eventoDtoId != null) ? ResponseEntity.ok(eventoDtoId) : ResponseEntity.notFound().build();
  }

  @PostMapping("/create-event")
  //solo dejo al cliente que cree eventos con los atributos que yo quiero
  public ResponseEntity<EventoDto> crearEvento(@RequestBody EventoDto eventoDto) {
      EventoDto eventoCreado = eventoService.crearEvento(eventoDto);
      return eventoCreado != null ? ResponseEntity.ok(eventoCreado) : ResponseEntity.notFound().build(); 
      //devuelvo un HTPP 200 si se creo correctamente un evento y caso contrario devuelvo un 400
  }

  @PutMapping("/{id}")
  public ResponseEntity<EventoDto> actualizarEvento(@PathVariable Long id, @RequestBody EventoDto eventoDto) {
    EventoDto eventAct = eventoService.actEvento(id, eventoDto);
    return   (eventAct!= null) ? ResponseEntity.ok(eventAct) : ResponseEntity.notFound().build();
      
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminarEvento(@PathVariable Long id) {
      return eventoService.eliminarEvento(id)
          ? ResponseEntity.noContent().build()
          : ResponseEntity.badRequest().build();
  }
    
  @PostMapping("/{id}/artists")
  public ResponseEntity<EventoDto> agregarArtista(
          @PathVariable Long id,
          @RequestParam Long idArtista) {

      // deberías buscar el artista desde su service/repo
      Artista artista = artistaService.findById(idArtista);

      EventoDto dto = eventoService.agregarArtista(id, artista);

      return dto != null
          ? ResponseEntity.ok(dto)
          : ResponseEntity.badRequest().build();
  }

  @DeleteMapping("/{id}/artist/{idArtista}")
  public ResponseEntity<EventoDto> removerArtista(
          @PathVariable Long id,
          @PathVariable Long idArtista) {

      EventoDto dto = eventoService.removerArtista(id, idArtista);

      return dto != null
          ? ResponseEntity.ok(dto)
          : ResponseEntity.badRequest().build();
  }

  @PutMapping("/{id}/confirmed")
  public ResponseEntity<EventoDto> confirmarEvento(@PathVariable Long id) {
      EventoDto dto = eventoService.confirmarEvento(id);

      return dto != null
          ? ResponseEntity.ok(dto)
          : ResponseEntity.badRequest().build();
  }

  @PutMapping("/{id}/rescheduled")
  public ResponseEntity<EventoDto> reprogramarEvento(
          @PathVariable Long id,
          @RequestParam String start_date) {

      LocalDate fecha = LocalDate.parse(start_date);

      EventoDto dto = eventoService.reprogramarEvento(id, fecha);

      return dto != null
          ? ResponseEntity.ok(dto)
          : ResponseEntity.badRequest().build();
  }

  @PutMapping("/{id}/canceled")
  public ResponseEntity<EventoDto> cancelarEvento(@PathVariable Long id) {
      EventoDto dto = eventoService.cancelarEvento(id);

      return dto != null
          ? ResponseEntity.ok(dto)
          : ResponseEntity.badRequest().build();
  }

}
  

//Solo puede acceder un admin a este controlador.