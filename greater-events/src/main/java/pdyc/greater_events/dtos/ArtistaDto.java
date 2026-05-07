package pdyc.greater_events.dtos;

import pdyc.greater_events.enums.Genero;
//DTO que solo va a manipular el nombre y genero del artista.
//Los DTOS se usan para no exponer toda la informacion de una clase, sino 
// solo lo necesario para cada caso. En este caso, el DTO de Artista solo va 
// a manipular el nombre y genero del artista, y no va a exponer toda la 
// informacion de la clase Artista. 
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
