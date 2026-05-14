package pdyc.greater_events.services;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import pdyc.greater_events.dtos.AdminUserDto;
import pdyc.greater_events.dtos.AdminUserRequestDto;

@Service
public class KeycloakAdminService {

    private final Keycloak keycloak;
    private final String realm;

    public KeycloakAdminService(Keycloak keycloak, @Value("${keycloak.realm}") String realm) {
        this.keycloak = keycloak;
        this.realm = realm;
    }

    public CompletableFuture<List<AdminUserDto>> listUsers() {
        //Spring delega la tarea y sigue. Para no esperar a Keycloak, se ejecuta en otro hilo y devuelve un CompletableFuture que se completará cuando la tarea termine.
        return CompletableFuture.supplyAsync(() ->
                keycloak.realm(realm).users().list().stream() //lista los usuarios de keycloak.
                        .map(user -> toDto(user))//convierte cada UserRepresentation a AdminUserDto
                        .collect(Collectors.toList())   //los convierte en una lista
        );
    }

    public CompletableFuture<AdminUserDto> createUser(AdminUserRequestDto request) {
        return CompletableFuture.supplyAsync(() -> {
            //creo un objeto UserRepresentation que es el formato de Keycloak.
            UserRepresentation user = new UserRepresentation();
            user.setUsername(request.getUsername());
            user.setEnabled(true);
            //creo el usuario en Keycloak y obtengo la respuesta. Uso try-with-resources para asegurarme de cerrar la respuesta.
            try (Response response = keycloak.realm(realm).users().create(user)) {
                if (response.getStatus() != 201) {
                    throw new RuntimeException("Error creando usuario en Keycloak: " + response.getStatus());
                }
                //creo el ID creado.
                String createdId = CreatedResponseUtil.getCreatedId(response);
                if (createdId == null) {
                    throw new RuntimeException("No se obtuvo el id del usuario creado en Keycloak");
                }
                //creo la contraseña para el usuario creado.
                CredentialRepresentation credential = new CredentialRepresentation();
                credential.setType(CredentialRepresentation.PASSWORD);
                credential.setValue(request.getPassword());
                credential.setTemporary(false);
                keycloak.realm(realm).users().get(createdId).resetPassword(credential);

                UserRepresentation created = keycloak.realm(realm).users().get(createdId).toRepresentation();
                //devuelvo el dto del usuario creado.
                return toDto(created);
            }
        });
    }

    public CompletableFuture<Void> deleteUser(String id) {
        //uso runAsync ya que ejecuto una tarea asincrona sin devolver un resultado.
        return CompletableFuture.runAsync(() -> keycloak.realm(realm).users().delete(id));
    }

    // Método auxiliar para convertir UserRepresentation a AdminUserDto
    private AdminUserDto toDto(UserRepresentation user) {
        return new AdminUserDto(
                user.getId(),
                user.getUsername(),
                Boolean.TRUE.equals(user.isEnabled())
        );
    }
}


/*Por otro lado, `supplyAsync()` es un método que se utiliza para ejecutar 
de forma asíncrona una tarea que produce un resultado.  */