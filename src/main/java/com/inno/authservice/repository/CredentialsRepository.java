package com.inno.authservice.repository;

import com.inno.authservice.entity.Credentials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CredentialsRepository extends JpaRepository<Credentials, Long> {

    Optional<Credentials> findByLogin(String login);

    Optional<Credentials> findByUserId(Long userId);

    boolean existsByLogin(String login);
}
