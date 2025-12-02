package example.namdinh.repository;


import com.google.api.services.gmail.Gmail;
import example.namdinh.entity.User;
import example.namdinh.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    List<UserSession> findByUserAndActiveTrue(User user);

}

