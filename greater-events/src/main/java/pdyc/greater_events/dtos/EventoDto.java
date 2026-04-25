package pdyc.greater_events.dtos;

import java.time.LocalDate;
import java.util.List;

import pdyc.greater_events.entities.Artista;
//import pdyc.greater_events.entities.StateEvento;
import pdyc.greater_events.enums.EventState;

public class EventoDto {
    private String nombreDto;
    private String descripcionDto;
    private LocalDate fechaRealizacionDto;
    private EventState estadoDto;
    private int cantArtistas;
    //private List<Artista> artistasDto;

    public EventoDto(){}

    public EventoDto(String nombreDto,String descripcionDto ,LocalDate fechaRealizacionDto) {
        this.nombreDto = nombreDto;
        this.descripcionDto = descripcionDto;
        this.fechaRealizacionDto = fechaRealizacionDto;
    }

     public EventoDto(String nombreDto,String descripcionDto ,LocalDate fechaRealizacionDto, EventState estadoDto,int cantArtistas) {
        this.nombreDto = nombreDto;
        this.descripcionDto = descripcionDto;
        this.fechaRealizacionDto = fechaRealizacionDto;
        this.estadoDto = estadoDto;
        this.cantArtistas = cantArtistas;
    }

    public EventoDto(String nombreDto, LocalDate fechaRealizacionDto,EventState estadoDto,int cantArtistas) {
        this.nombreDto = nombreDto;
        this.fechaRealizacionDto = fechaRealizacionDto;
        this.estadoDto = estadoDto;
        this.cantArtistas=cantArtistas;    
    }

    public String getNombreDto() {
      return nombreDto;
    }

    public void setNombreDto(String nombreDto) {
      this.nombreDto = nombreDto;
    }

    public LocalDate getFechaRealizacionDto() {
      return fechaRealizacionDto;
    }

    public void setFechaRealizacionDto(LocalDate fechaRealizacionDto) {
      this.fechaRealizacionDto = fechaRealizacionDto;
    }

    public EventState getEstadoDto() {
      return estadoDto;
    }

    public void setEstadoDto(EventState estadoDto) {
      this.estadoDto = estadoDto;
    }

    

    public String getDescripcionDto() {
      return descripcionDto;
    }

    public void setDescripcionDto(String descripcionDto) {
      this.descripcionDto = descripcionDto;
    }

    public int getCantArtistas() {
      return cantArtistas;
    }

    public void setCantArtistas(int cantArtistas) {
      this.cantArtistas = cantArtistas;
    }
    

}
