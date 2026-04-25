package pdyc.greater_events.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pdyc.greater_events.entities.Evento;
import pdyc.greater_events.enums.EventState;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByEventState(EventState state);


    Evento findByIdEvento(Long id);
}
