package pdyc.greater_events.services;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import java.util.Collections;
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
        return CompletableFuture.supplyAsync(() ->
                keycloak.realm(realm).users().list().stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
    }
    
    public CompletableFuture<AdminUserDto> createUser(AdminUserRequestDto request) {
        return CompletableFuture.supplyAsync(() -> {
            UserRepresentation user = new UserRepresentation();
            user.setUsername(request.getUsername());
            if (request.getEmail() != null) {
                user.setEmail(request.getEmail());
                user.setEmailVerified(false);
            }
            user.setEnabled(true);

            try (Response response = keycloak.realm(realm).users().create(user)) {
                if (response.getStatus() != 201) {
                    throw new RuntimeException("Error creando usuario en Keycloak: " + response.getStatus());
                }
                
                String createdId = CreatedResponseUtil.getCreatedId(response);
                if (createdId == null) {
                    throw new RuntimeException("No se obtuvo el id del usuario creado en Keycloak");
                }
                
                CredentialRepresentation credential = new CredentialRepresentation();
                credential.setType(CredentialRepresentation.PASSWORD);
                credential.setValue(request.getPassword());
                credential.setTemporary(false);
                keycloak.realm(realm).users().get(createdId).resetPassword(credential);
                
                // Asignar rol de realm 'ROLE_USER'
                try {
                    RoleRepresentation role = keycloak.realm(realm).roles().get("ROLE_USER").toRepresentation();
                    if (role != null) {
                        keycloak.realm(realm).users().get(createdId).roles().realmLevel().add(Collections.singletonList(role));
                    }
                } catch (Exception e) { // 💡 Cambiado a Exception genérica
                    // Rollback preventivo también para usuario común
                    try { keycloak.realm(realm).users().delete(createdId); } catch (Exception ignored) {}
                    throw new RuntimeException("No se pudo asignar el rol de usuario. Registro cancelado: " + e.getMessage());
                }

                UserRepresentation created = keycloak.realm(realm).users().get(createdId).toRepresentation();
                return toDto(created);
            }
        });
    }

    public CompletableFuture<AdminUserDto> createAdminUser(AdminUserRequestDto request) {
        return CompletableFuture.supplyAsync(() -> {
            UserRepresentation user = new UserRepresentation();
            user.setUsername(request.getUsername());
            user.setEnabled(true);
            if (request.getEmail() != null) {
                user.setEmail(request.getEmail());
                user.setEmailVerified(false);
            }

            String createdId = null;
            try (Response response = keycloak.realm(realm).users().create(user)) {
                if (response.getStatus() != 201) {
                    throw new RuntimeException("Error creando usuario en Keycloak: " + response.getStatus());
                }
                createdId = CreatedResponseUtil.getCreatedId(response);
                if (createdId == null) {
                    throw new RuntimeException("No se obtuvo el id del usuario creado en Keycloak");
                }
                
                CredentialRepresentation credential = new CredentialRepresentation();
                credential.setType(CredentialRepresentation.PASSWORD);
                credential.setValue(request.getPassword());
                credential.setTemporary(false);
                keycloak.realm(realm).users().get(createdId).resetPassword(credential);

                // Asignar rol de realm 'ROLE_ADMIN'
                try {
                    RoleRepresentation role = keycloak.realm(realm).roles().get("ROLE_ADMIN").toRepresentation();
                    if (role != null) {
                        keycloak.realm(realm).users().get(createdId).roles().realmLevel().add(Collections.singletonList(role));
                    }
                } catch (Exception e) {
                    // ROLLBACK MANUAL
                    try {
                        keycloak.realm(realm).users().delete(createdId);
                    } catch (Exception ex) {
                        throw new RuntimeException("Error asignando rol admin y falló la eliminación del usuario huérfano: " + ex.getMessage());
                    }
                    // 💡 OBLIGATORIO: Cortar el flujo lanzando la excepción original si el delete fue exitoso
                    throw new RuntimeException("No se pudo asignar el rol de administrador. Registro cancelado: " + e.getMessage());
                }

                UserRepresentation created = keycloak.realm(realm).users().get(createdId).toRepresentation();
                return toDto(created);
            } 
        });
    }
    
    public CompletableFuture<Void> deleteUser(String id) {
        return CompletableFuture.runAsync(() -> keycloak.realm(realm).users().delete(id));
    }

    private AdminUserDto toDto(UserRepresentation user) {
        return new AdminUserDto(
                user.getId(),
                user.getUsername(),
                Boolean.TRUE.equals(user.isEnabled())
        );
    }
}