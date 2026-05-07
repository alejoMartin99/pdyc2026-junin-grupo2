package pdyc.greater_events.services;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

//import javax.management.RuntimeErrorException;

//import org.apache.el.stream.Optional;
import org.springframework.stereotype.Service;

import pdyc.greater_events.dtos.ArtistaDto;
import pdyc.greater_events.dtos.EventoDto;
import pdyc.greater_events.entities.Artista;
import pdyc.greater_events.entities.Evento;
import pdyc.greater_events.enums.EventState;
import pdyc.greater_events.exceptions.EventoException;
import pdyc.greater_events.repositories.EventoRepository;

@Service
public class EventoService {

    private final EventoRepository repository;

    public EventoService(EventoRepository repository) {
        this.repository = repository;
    }

    //solo 3 parametros ya que el evento no tendrá inicialmente artistas asignados y su estado
    //inicial será Tentative.
    public EventoDto crearEvento(EventoDto eventoDto) {
        //creo un objeto de tipo dto que se inicializa con los datos del request del cliente. Y los paso como parametros para el constructor del Evento.
        Evento evento = new Evento(eventoDto.getNombreDto(),eventoDto.getDescripcionDto(),eventoDto.getFechaRealizacionDto(), EventState.Tentative);
        repository.save(evento); //guardo el evento con todos los parametros en la bd
        return devolverEventoDtoCreado(evento.getIdEvento()); //devuelvo el eventoDto;
    }

    //actualizo el evento segun el id, que debe pertenecer a un evento. 
    //Y el evento para actualizarse debe estar en estado Tentative.
    public EventoDto actEvento(Long id, EventoDto eventoDto){
        Evento evento = repository.findByIdEvento(id);
        if (evento == null) {
            throw new EventoException("Evento no encontrado");
        }

        if (evento.getEventState() != EventState.Tentative) {
            throw new EventoException("Solo se pueden actualizar eventos en estado Tentative");
        }

        evento.setNombre(eventoDto.getNombreDto());
        evento.setFechaRealizacion(eventoDto.getFechaRealizacionDto());
        evento.setDescripcion(eventoDto.getDescripcionDto());

        Evento actualizado = repository.save(evento);

        return devolverEventoDtoPorId(actualizado.getIdEvento());
    }

    //listo los eventos segun su estado y sino los listo a todos.
    public List<EventoDto> devolverEventos(EventState state){
        List<Evento> eventos = (state != null)
            ? repository.findByEventState(state)
            : repository.findAll();
        return transformarDto(eventos);
    }

    //elimino evento por ID si este se encuentra y esta en estado Tentative.
    public boolean eliminarEvento(Long id) {
        Evento evento = repository.findByIdEvento(id);
        if (evento == null) {
            throw new EventoException("Evento no encontrado");
        }

        if (evento.getEventState() != EventState.Tentative) {
            return false;
        }

        repository.delete(evento);
        return true;
    }
    //remuesto a un art de un evento. Solo se remueve un artista si el evento esta en Tentative.
    public EventoDto removerArtista(Long eventId, Long idArtista) {
        Evento evento = repository.findByIdEvento(eventId);
        Boolean artistaEncontrado = false;
        if (evento == null) {
            throw new EventoException("Evento no encontrado");
        }

        if (evento.getEventState() != EventState.Tentative) {
            throw new EventoException("Solo se pueden remover artistas de eventos en estado Tentative");
        }
        //recorro los artistas y cuando encuentro al artista a eliminar, lo borro de la bd.
        for(Artista art : evento.getArtistas()){
            if(art.getIdArtista().equals(idArtista)){
                artistaEncontrado = true;
                evento.getArtistas().remove(art);
                repository.save(evento);
            }
        }

        if (!artistaEncontrado) {
            throw new EventoException("Artista no encontrado en el evento");
        }

        return devolverEventoDtoPorId(eventId);
    }
    //confirmo un evento, solo se pueden confirmar eventos en estado Tentative.
    public EventoDto confirmarEvento(Long id) {
        Evento evento = repository.findByIdEvento(id);
        if (evento == null) {
            throw new EventoException("Evento no encontrado");
        }

        if (evento.getEventState() != EventState.Tentative) {
            throw new EventoException("Solo se pueden confirmar eventos en estado Tentative");
        }

        evento.setEventState(EventState.Confirmed);
        repository.save(evento);
        return devolverEventoDtoPorId(id);
    }
    //solo se pueden reprogramar eventos en estado Confirmed o Reprogrammed, 
    // y la nueva fecha debe ser igual o posterior a la fecha original del evento.
    public EventoDto reprogramarEvento(Long id, LocalDate nuevaFecha) {
        Evento evento = repository.findByIdEvento(id);
        if (evento == null) {
            throw new EventoException("Evento no encontrado");
        }

        if (evento.getEventState() != EventState.Confirmed &&
            evento.getEventState() != EventState.Rescheduled) {
            throw new EventoException("Solo se pueden reprogramar eventos en estado Confirmed o Rescheduled");
        }

        if(evento.getEventState() == EventState.Confirmed || evento.getEventState() == EventState.Rescheduled){
            if (nuevaFecha.isBefore(evento.getFechaRealizacion())) {
                throw new EventoException("La nueva fecha debe ser igual o posterior a la fecha original del evento");
            }
        }
        evento.setFechaRealizacion(nuevaFecha);
        evento.setEventState(EventState.Rescheduled);
        repository.save(evento);
        return devolverEventoDtoPorId(id);
    }

    public EventoDto cancelarEvento(Long id) {
        Evento evento = repository.findByIdEvento(id);
        if (evento == null) {
            throw new EventoException("Evento no encontrado");
        }

        //si el evento no esta confirmado o reprogramado, no se puede cancelar
        if (evento.getEventState() != EventState.Confirmed &&
            evento.getEventState() != EventState.Rescheduled) {
            throw new EventoException("Solo se pueden cancelar eventos en estado Confirmed o Rescheduled");    
        }

        evento.setEventState(EventState.Cancelled);
        repository.save(evento);
        return devolverEventoDtoPorId(id);
    }

    public EventoDto agregarArtista(Long eventId, Artista artista) {
        Evento evento = repository.findByIdEvento(eventId);
        if (evento == null) {
            throw new EventoException("Evento no encontrado");
        }
        
        if (evento.getEventState() != EventState.Tentative) {
            throw new EventoException("Solo se pueden agregar artistas a eventos en estado Tentative");
        }

        evento.getArtistas().add(artista);
        repository.save(evento);
        return devolverEventoDtoPorId(eventId);
    }

    public EventoDto devolverEventoDtoPorId(Long id){
        return devolverEventoDtoCreado(id);
    }

    public EventoDto devolverEventoDtoCreado(Long id){
        Evento evento = repository.findByIdEvento(id);
        EventoDto eventoDto = new EventoDto(evento.getNombre(),evento.getDescripcion(),evento.getFechaRealizacion(),evento.getEventState(),evento.getArtistas().size());
        return eventoDto;

    }

    //transformo la lista de clase Evento a una de List<EventoDto>, en donde 
    // muestro nombre, fecha, estado y la cantidad de artistas
    public List<EventoDto> transformarDto(List<Evento> eventos){
        return eventos.stream()
            //transformo cada objeto evento e de la lista en un objeto de tipo EventoDto y lo inicializo a cada objeto de tipo EventoDto
            .map(e -> new EventoDto(
                    e.getNombre(),
                    e.getFechaRealizacion(),
                    e.getEventState(),
                    e.getArtistas().size()
            ))
           .collect(Collectors.toList()); // lo transformo en una lista
    }
}
