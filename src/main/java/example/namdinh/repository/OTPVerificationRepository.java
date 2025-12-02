package example.namdinh.repository;

import example.namdinh.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OTPVerificationRepository extends JpaRepository<Otp, String> {
    Optional<Otp> findByEmail(String email);
    void deleteByEmail(String email);
}