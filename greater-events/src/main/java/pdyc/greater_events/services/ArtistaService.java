package pdyc.greater_events.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import pdyc.greater_events.dtos.ArtistaDto;
import pdyc.greater_events.entities.Artista;
import pdyc.greater_events.enums.Genero;
import pdyc.greater_events.exceptions.ArtistaException;
import pdyc.greater_events.repositories.ArtistRepository;

@Service
public class ArtistaService {

    private final ArtistRepository repository;

    public ArtistaService(ArtistRepository repository) {
        this.repository = repository;
    }

    public List<ArtistaDto> getArtists(Genero genero) {
        if (genero != null){
           return repository.findByGenero(genero).stream().map(a -> new ArtistaDto(a.getNombre(), a.getGenero())).collect(Collectors.toList());
        }
        return repository.findAll().stream().map(a -> new ArtistaDto(a.getNombre(), a.getGenero())).collect(Collectors.toList());
    }

    public ArtistaDto createArtist(ArtistaDto artist) {
        //cambio su estado y lo guardo en la bd
        Artista artista = new Artista(artist.getNombreDto(), artist.getGeneroDto());
        artista.setActivo(true); 
        Artista artistaSave = repository.save(artista);
        ArtistaDto artistaDto = new ArtistaDto(artistaSave.getNombre(), artistaSave.getGenero());
        return artistaDto;
    }


    public ArtistaDto updateArtist(Long id, ArtistaDto artist) {
        Artista existing = repository.findById(id)
            .orElseThrow(() -> new ArtistaException("Artista no encontrado"));
        //busco al artista por su id y si no lo encuentra lanzo una RuntimeException
        if (!existing.getActivo()) { //no se actualiza artista que esta desactivado
            throw new ArtistaException("No se puede editar un artista inactivo");
        }
        //si esta todo ok
        existing.setNombre(artist.getNombreDto());
        existing.setGenero(artist.getGeneroDto());
        //lo guardo en la bd
        Artista artista = repository.save(existing);
        ArtistaDto artistaDto = new ArtistaDto(artista.getNombre(), artista.getGenero());
        return artistaDto;
    }

    public Artista findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void deleteArtist(Long id) {
        Artista artist = repository.findById(id)
            .orElseThrow(() -> new ArtistaException("Artista no encontrado")); //lanzo excepcion sino encuentro al artista

        if (artist.getEventos().isEmpty()) { //si no esta asociado a eventos, se desactiva y se elimina
            artist.setActivo(false);
            repository.delete(artist);
        }
    }
}