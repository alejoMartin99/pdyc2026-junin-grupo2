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

@RestController
@RequestMapping("/admin/keycloak")
public class KeycloakAdminController {

    private final KeycloakAdminService keycloakAdminService;

    public KeycloakAdminController(KeycloakAdminService keycloakAdminService) {
        this.keycloakAdminService = keycloakAdminService;
    }

    @GetMapping("/users")
    public CompletableFuture<ResponseEntity<List<AdminUserDto>>> getUsers() {
        return keycloakAdminService.listUsers().thenApply(ResponseEntity::ok);
    }

    @PostMapping("/users")
    public CompletableFuture<ResponseEntity<AdminUserDto>> createUser(@RequestBody AdminUserRequestDto request) {
        return keycloakAdminService.createUser(request)
                .thenApply(ResponseEntity::ok);
    }

    @DeleteMapping("/users/{id}")
    public CompletableFuture<ResponseEntity<Void>> deleteUser(@PathVariable String id) {
        return keycloakAdminService.deleteUser(id)
                .thenApply(ignored -> ResponseEntity.noContent().build());
    }
}
