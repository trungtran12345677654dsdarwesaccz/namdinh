package example.namdinh.repository;


import example.namdinh.entity.User;
import example.namdinh.enumeration.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<User, Integer> {
    Optional<User> findByRole(UserRole role);
}
