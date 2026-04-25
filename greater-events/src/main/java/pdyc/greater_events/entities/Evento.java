package pdyc.greater_events.entities;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import pdyc.greater_events.enums.EventState;

@Entity
@Table(name = "evento")
public class Evento {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long idEvento;

  private String nombre;
  private String descripcion;
  private LocalDate fechaRealizacion;

  @Enumerated(EnumType.STRING)
  private EventState eventState;

  @ManyToMany
  @JoinTable(
      name = "event_artist",
      joinColumns = @JoinColumn(name = "event_id"),
      inverseJoinColumns = @JoinColumn(name = "artist_id")
  )
  private List<Artista> artistas = new ArrayList<>();

  public Evento(){}

  public Evento(String nombre, String descripcion, LocalDate fechaRealizacion, EventState eventState) {
    this.nombre = nombre;
    this.descripcion = descripcion;
    this.fechaRealizacion = fechaRealizacion;
    this.eventState = eventState;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public LocalDate getFechaRealizacion() {
    return fechaRealizacion;
  }

  public void setFechaRealizacion(LocalDate fechaRealizacion) {
    this.fechaRealizacion = fechaRealizacion;
  }

  public EventState getEventState() {
    return eventState;
  }

  public void setEventState(EventState eventState) {
    this.eventState = eventState;
  }

  public List<Artista> getArtistas() {
    return artistas;
  }

  public void setArtistas(List<Artista> artistas) {
    this.artistas = artistas;
  }

  public Long getIdEvento() {
    return idEvento;
  }

  public void setIdEvento(Long idEvento) {
    this.idEvento = idEvento;
  }

  // getters y setters
}