package pdyc.greater_events.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    //extrae los roles del JWT de Keycloak para que Spring Security sepa que request debe permitir.
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        Object realmAccess = jwt.getClaim("realm_access");
        if (realmAccess instanceof Map) {
            Map<?,?> realm = (Map<?,?>) realmAccess;
            Object roles = realm.get("roles");
            if (roles instanceof List) {
                for (Object r : (List<?>) roles) {
                    String role = String.valueOf(r).toUpperCase();
                    if (!role.startsWith("ROLE_")) {
                        role = "ROLE_" + role;
                    }
                    authorities.add(new SimpleGrantedAuthority(role));
                }
            }
        }
        // also check resource_access.{client}.roles
        Object resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess instanceof Map) {
            Map<?,?> resources = (Map<?,?>) resourceAccess;
            for (Object val : resources.values()) {
                if (val instanceof Map) {
                    Object rls = ((Map<?,?>) val).get("roles");
                    if (rls instanceof List) {
                        for (Object r : (List<?>) rls) {
                            String role = String.valueOf(r).toUpperCase();
                            if (!role.startsWith("ROLE_")) {
                                role = "ROLE_" + role;
                            }
                            authorities.add(new SimpleGrantedAuthority(role));
                        }
                    }
                }
            }
        }
        return authorities; //retorno mi coleccion de roles.
    }
}

/*KeycloakRoleConverter
Convierte roles del JWT de Keycloak en GrantedAuthority de Spring
(permiso que se le da a un usuario autenticado).

Lee realm_access.roles y resource_access.{client}.roles del token JWT
Transforma cada rol a ROLE_<nombreDelRol> para que Spring lo entienda
Ejemplo: rol admin → ROLE_ADMIN */