package pdyc.greater_events.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pdyc.greater_events.dtos.AdminUserRequestDto;
import pdyc.greater_events.dtos.AdminUserDto;
import pdyc.greater_events.services.KeycloakAdminService;
import pdyc.greater_events.services.UserService;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final KeycloakAdminService keycloakAdminService;
    private final UserService userService;

    public AuthController(KeycloakAdminService keycloakAdminService, UserService userService) {
        this.keycloakAdminService = keycloakAdminService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public CompletableFuture<ResponseEntity<AdminUserDto>> register(@RequestBody AdminUserRequestDto request) {
        return keycloakAdminService.createUser(request).thenApply(dto -> {
            // save local user record
            userService.register(dto.getUsername(), request.getEmail());
            return ResponseEntity.ok(dto);
        });
    }
}

/*AuthController
Endpoint público de registro de usuarios.

POST /auth/register: recibe username, 
password, email → crea usuario en Keycloak y registra en BD local */