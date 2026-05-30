package pdyc.greater_events.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import pdyc.greater_events.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
