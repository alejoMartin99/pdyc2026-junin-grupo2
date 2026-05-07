package pdyc.greater_events.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import pdyc.greater_events.enums.Genero;

//creo a artista como entidad, con su tabla en la bd.
@Entity
@Table(name = "artistas")
public class Artista {

    //creo el id idArtista como PK y que sea autoincrementable.
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idArtista;
    //nombre del artista
    private String nombre;
    //genero musical del artista, el cual es un enum.
    @Enumerated(EnumType.STRING)
    private Genero genero;

    //estado del artista (activo o no), si esta activo es porque esta 
    // asociado a un evento/ eventos.
    private Boolean activo = false;

    //un artista tiene una coleccion de eventos.
    @ManyToMany(mappedBy = "artistas") 
    //mi tabla artistas es dueña de la relacion con eventos.
    private List<Evento> eventos = new ArrayList<>();
    

    public Artista() {
    }

    //Constructor el cual permite instanciar a un artista con su nombre y geneto musical
    public Artista(String nombre, Genero genero) {
        this.nombre = nombre;
        this.genero = genero;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public List<Evento> getEventos() {
        return eventos;
    }

    public void setEventos(List<Evento> eventos) {
        this.eventos = eventos;
    }

    //getters and setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public Long getIdArtista() {
        return idArtista;
    }

    public void setIdArtista(Long idArtista) {
        this.idArtista = idArtista;
    }

}
