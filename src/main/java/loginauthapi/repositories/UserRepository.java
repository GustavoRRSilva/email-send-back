package loginauthapi.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import loginauthapi.domain.User;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
}
