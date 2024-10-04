package mg.mherifaniry.Tickets.dao;

import mg.mherifaniry.Tickets.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);
    User findByUsername(String username);
}
