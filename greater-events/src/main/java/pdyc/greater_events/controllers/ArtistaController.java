package pdyc.greater_events.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pdyc.greater_events.dtos.ArtistaDto;
import pdyc.greater_events.entities.Artista;
import pdyc.greater_events.enums.Genero;
import pdyc.greater_events.services.ArtistaService;

@RestController
@RequestMapping("/admin/artists")
public class ArtistaController {

    @Autowired
    private ArtistaService service;

    public ArtistaController(ArtistaService service) {
        this.service = service;
    }

    //endpoint para obtener artistas, con filtro opcional por genero
    @GetMapping("/getArtists")
    public ResponseEntity<List<ArtistaDto>> getArtists(@RequestParam(required = false) Genero genero) {
        List<ArtistaDto> artistasDtos = service.getArtists(genero);
        return ResponseEntity.ok(artistasDtos);
    }
    //endpoint para crear un nuevo artista, recibe un objeto Artista en el cuerpo de la solicitud
    @PostMapping("/createArtist")
    public ResponseEntity<ArtistaDto> create(@RequestBody ArtistaDto artist) {
        ArtistaDto artistaDto = service.createArtist(artist);
        return artistaDto != null ? ResponseEntity.ok(artistaDto) : ResponseEntity.badRequest().build();
    }
    //endpoint para actualizar un artista existente, recibe el id del artista a actualizar y un objeto Artista con los nuevos datos en el cuerpo de la solicitud
    @PutMapping("/{id}")
    public ResponseEntity<ArtistaDto> update(@PathVariable Long id, @RequestBody ArtistaDto artist) {
        ArtistaDto updated = service.updateArtist(id, artist);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }
    //endpoint para eliminar un artista, recibe el id del artista a eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteArtist(id);
        return ResponseEntity.noContent().build();
    }
}