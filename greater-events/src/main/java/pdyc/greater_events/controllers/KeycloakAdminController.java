package pdyc.greater_events.controllers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pdyc.greater_events.dtos.AdminUserDto;
import pdyc.greater_events.dtos.AdminUserRequestDto;
import pdyc.greater_events.services.KeycloakAdminService;
import pdyc.greater_events.services.UserService;

@RestController
@RequestMapping("/admin/keycloak")
public class KeycloakAdminController {

    private final KeycloakAdminService keycloakAdminService;
    private final UserService userService;

    public KeycloakAdminController(KeycloakAdminService keycloakAdminService, UserService userService) {
        this.keycloakAdminService = keycloakAdminService;
        this.userService = userService;
    }

    @GetMapping("/users")
    public CompletableFuture<ResponseEntity<List<AdminUserDto>>> getUsers() {
        return keycloakAdminService.listUsers().thenApply(ResponseEntity::ok);
    }

    @PostMapping("/create-admin")
    public CompletableFuture<ResponseEntity<AdminUserDto>> createUser(@RequestBody AdminUserRequestDto request) {
        return keycloakAdminService.createAdminUser(request).thenApply(dto -> {
            userService.register(dto.getUsername(), request.getEmail());
            return ResponseEntity.ok(dto);
        });
    }

    @DeleteMapping("/users/{id}")
    public CompletableFuture<ResponseEntity<Void>> deleteUser(@PathVariable String id) {
        return keycloakAdminService.deleteUser(id)
                .thenApply(ignored -> ResponseEntity.noContent().build());
    }
}


/*KeycloakAdminController
Endpoints protegidos para admin (/admin/keycloak).

GET /admin/keycloak/users: lista usuarios (requiere rol ADMIN)
POST /admin/keycloak/create-admin: crea usuario admin (requiere rol ADMIN) → llama a createAdminUser() y guarda usuario local
DELETE /admin/keycloak/users/{id}: borra usuario (requiere rol ADMIN) */