package pdyc.greater_events.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pdyc.greater_events.entities.Evento;
import pdyc.greater_events.enums.EventState;

//Los nombres de los metodos deben coincidir con los nombres de los atributos para que se implementen correctamente.
//asi la bd hace la consulta correctamente.
public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByEventState(EventState state);


    Evento findByIdEvento(Long id);
}
