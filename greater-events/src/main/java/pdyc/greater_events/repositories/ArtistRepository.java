package pdyc.greater_events.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pdyc.greater_events.entities.Artista;
import pdyc.greater_events.enums.Genero;

public interface ArtistRepository extends JpaRepository<Artista, Long> {
    List<Artista> findByGenero(Genero genero);
    List<Artista> findByNombre(String nombre); //buscar por nombre del artista

}
