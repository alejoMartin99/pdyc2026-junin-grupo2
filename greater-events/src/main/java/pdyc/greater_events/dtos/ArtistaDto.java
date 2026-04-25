package pdyc.greater_events.dtos;

import pdyc.greater_events.enums.Genero;

public class ArtistaDto {
    private String nombreDto;
    private Genero generoDto;

    public ArtistaDto(){}

    public ArtistaDto(String nombreDto, Genero generoDto) {
        this.nombreDto = nombreDto;
        this.generoDto = generoDto;
    }

    public String getNombreDto() {
        return nombreDto;
    }

    public void setNombreDto(String nombreDto) {
        this.nombreDto = nombreDto;
    }

    public Genero getGeneroDto() {
        return generoDto;
    }

    public void setGeneroDto(Genero generoDto) {
        this.generoDto = generoDto;
    }  
}
