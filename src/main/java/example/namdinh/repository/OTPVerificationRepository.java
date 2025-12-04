package example.namdinh.repository;

import example.namdinh.entity.Otp;
import example.namdinh.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OTPVerificationRepository extends JpaRepository<Otp, String> {
    Optional<Otp> findByEmail(String email);
    Optional<Otp> findByUser(User user);
    void deleteByEmail(String email);
}